package com.financemanage.hmoneylover.ui.view.home


import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.financemanage.hmoneylover.viewmodel.TransactionViewModel
import com.google.android.material.snackbar.Snackbar
import com.financemanage.hmoneylover.R
import com.financemanage.hmoneylover.adapter.TransactionAdapter
import com.financemanage.hmoneylover.databinding.FragmentHomeBinding
import com.financemanage.hmoneylover.model.Transaction
import com.financemanage.hmoneylover.resources.ViewState
import com.financemanage.hmoneylover.ui.view.base.BaseFragment
import com.financemanage.hmoneylover.utils.hide
import com.financemanage.hmoneylover.utils.indianRupee
import com.financemanage.hmoneylover.utils.show
import com.financemanage.hmoneylover.utils.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, TransactionViewModel>() {
    private lateinit var transactionAdapter: TransactionAdapter
    override val viewModel: TransactionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerview()
        binding.btnAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addTransactionFragment)
        }

        binding.mainDashboardScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (abs(scrollY - oldScrollY) > 10) {
                    when {
                        scrollY > oldScrollY -> binding.btnAddTransaction.hide()
                        oldScrollY > scrollY -> binding.btnAddTransaction.show()
                    }
                }
            }
        )

        transactionAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("transaction", it)
            }
            findNavController().navigate(
                R.id.action_homeFragment_to_transactionDetailsFragment,
                bundle
            )
        }
        observeFilter()
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                when (it) {
                    is ViewState.Loading -> {
                    }
                    is ViewState.Success -> {
                        showAllViews()
                        onTransactionLoaded(it.transaction)
                        onTotalTransactionLoaded(it.transaction)
                    }
                    is ViewState.Error -> {
                        binding.root.snack(string = R.string.text_error)
                    }
                    is ViewState.Empty -> {
                        hideAllViews()
                    }
                }
            }
        }
        swipeToDelete()
    }

    private fun observeFilter() = with(binding) {
        lifecycleScope.launchWhenCreated {
            viewModel.transactionFilter.collect { filter ->
                when (filter) {
                    "Default" -> {
                        totalBalanceView.totalBalanceTitle.text =
                            getString(R.string.text_total_balance)
                        totalIncomeExpenseView.show()
                        incomeCardView.totalTitle.text = getString(R.string.text_total_income)
                        expenseCardView.totalTitle.text = getString(R.string.text_total_expense)
                        expenseCardView.totalIcon.setImageResource(R.drawable.ic_spend)
                    }
                    "Earning" -> {
                        totalBalanceView.totalBalanceTitle.text =
                            getString(R.string.text_total_income)
                        totalIncomeExpenseView.hide()
                    }
                    "Spending" -> {
                        totalBalanceView.totalBalanceTitle.text =
                            getString(R.string.text_total_expense)
                        totalIncomeExpenseView.hide()
                    }
                }
                viewModel.getAllTransaction(filter)
            }
        }
    }

    private fun setupRecyclerview() = with(binding) {
        transactionAdapter = TransactionAdapter()
        transactionRv.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun swipeToDelete() {
        val itemTouchHelperCallbacks = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val transaction = transactionAdapter.differ.currentList[position]
                val transactionItem = Transaction(
                    transaction.title,
                    transaction.amount,
                    transaction.transactionType,
                    transaction.tag,
                    transaction.date,
                    transaction.note,
                    transaction.createdAt,
                    transaction.id
                )
                viewModel.deleteTransaction(transactionItem)
                Snackbar.make(
                    binding.root,
                    getString(R.string.success_transaction_delete),
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction(getString(R.string.text_undo)) {
                        viewModel.insertTransaction(transactionItem)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallbacks).apply {
            attachToRecyclerView(binding.transactionRv)
        }
    }

    private fun onTotalTransactionLoaded(transactions: List<Transaction>) = with(binding) {
        val (totalIncome, totalExpense) = transactions.partition { it.transactionType == "Income" }
        val income = totalIncome.sumOf { it.amount }
        val expense = totalExpense.sumOf { it.amount }
        incomeCardView.total.text = "+ ".plus(indianRupee(income))
        expenseCardView.total.text = "- ".plus(indianRupee(expense))
        totalBalanceView.totalBalance.text = indianRupee(income - expense)
    }


    private fun showAllViews() = with(binding) {
        dashboardGroup.show()
        emptyStateLayout.hide()
        transactionRv.show()
    }

    private fun hideAllViews() = with(binding) {
        dashboardGroup.hide()
        transactionRv.show()
    }

    private fun onTransactionLoaded(list: List<Transaction>) =
        transactionAdapter.differ.submitList(list)


    override fun getViewBinding(
        inflator: LayoutInflater,
        container: ViewGroup?,
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflator, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.spinner_menu, menu)

        val item = menu.findItem(R.id.spinner)
        val spinner = item.actionView as Spinner

        val adapter = ArrayAdapter.createFromResource(
            applicationContext(),
            R.array.allFilters,
            R.layout.item_filter_dropdown
        )
        adapter.setDropDownViewResource(R.layout.item_filter_dropdown)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                lifecycleScope.launchWhenStarted {
                    when (position) {
                        0 -> {
                            viewModel.overall()
                            (view as TextView).setTextColor(resources.getColor(R.color.black))
                        }
                        1 -> {
                            viewModel.allIncome()
                            (view as TextView).setTextColor(resources.getColor(R.color.black))
                        }
                        2 -> {
                            viewModel.allExpense()
                            (view as TextView).setTextColor(resources.getColor(R.color.black))
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                lifecycleScope.launchWhenStarted {
                    viewModel.overall()
                }
            }
        }
    }
}