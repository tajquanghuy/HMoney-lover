package com.pigllet.hmoneylover.ui.view.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pigllet.hmoneylover.ui.view.base.BaseFragment
import com.pigllet.hmoneylover.utils.Constants
import com.pigllet.hmoneylover.utils.parseDouble
import com.pigllet.hmoneylover.utils.snack
import com.pigllet.hmoneylover.utils.transformIntoDatePicker
import com.pigllet.hmoneylover.viewmodel.TransactionViewModel
import com.pigllet.hmoneylover.R
import com.pigllet.hmoneylover.databinding.FragmentAddTransactionBinding
import com.pigllet.hmoneylover.model.Transaction
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddTransactionFragment : BaseFragment<FragmentAddTransactionBinding, TransactionViewModel>() {

    override val viewModel: TransactionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val transactionTypeAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.item_autocomplete_layout,
                Constants.transactionType
            )
        val tagsAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_autocomplete_layout,
            Constants.transactionTags
        )

        with(binding) {
            addTransactionLayout.etTransactionType.setAdapter(transactionTypeAdapter)
            addTransactionLayout.etTag.setAdapter(tagsAdapter)
            addTransactionLayout.etWhen.transformIntoDatePicker(
                requireContext(),
                "dd/MM/yyyy",
                Date()
            )

            btnSaveTransaction.setOnClickListener {
                binding.addTransactionLayout.apply {
                    val (title, amount, transactionType, tag, date, note) = getTransactionContent()
                    when {
                        title.isEmpty() -> {
                            this.etTitle.error = "Title shouldn't be empty"
                        }
                        amount.isNaN() -> {
                            this.etAmount.error = "Amount shouldn't be empty"
                        }
                        transactionType.isEmpty() -> {
                            this.etTransactionType.error = "You should provide Transaction type"
                        }
                        tag.isEmpty() -> {
                            this.etTag.error = "You should provide transaction tag"
                        }
                        date.isEmpty() -> {
                            this.etNote.error = "You should provide note for the transaction"
                        }
                        else -> {
                            viewModel.insertTransaction(getTransactionContent()).run {
                                binding.root.snack(
                                    string = R.string.success_expense_saved
                                )
                                findNavController().navigate(
                                    R.id.action_addTransactionFragment_to_homeFragment
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getTransactionContent(): Transaction = binding.addTransactionLayout.let {
        val title = it.etTitle.text.toString()
        val amount = parseDouble(it.etAmount.text.toString())
        val transactionType = it.etTransactionType.text.toString()
        val tag = it.etTag.text.toString()
        val date = it.etWhen.text.toString()
        val note = it.etNote.text.toString()

        return Transaction(title, amount, transactionType, tag, date, note)
    }

    override fun getViewBinding(
        inflator: LayoutInflater,
        container: ViewGroup?,
    ): FragmentAddTransactionBinding {
        return FragmentAddTransactionBinding.inflate(inflator, container, false)
    }
}