package com.financemanage.hmoneylover.ui.view.details

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.financemanage.hmoneylover.R
import com.financemanage.hmoneylover.databinding.FragmentTransactionDetailsBinding
import com.financemanage.hmoneylover.model.Transaction
import com.financemanage.hmoneylover.resources.DetailState
import com.financemanage.hmoneylover.ui.view.base.BaseFragment
import com.financemanage.hmoneylover.utils.cleanTextContent
import com.financemanage.hmoneylover.utils.indianRupee
import com.financemanage.hmoneylover.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDetailsFragment :
    BaseFragment<FragmentTransactionDetailsBinding, TransactionViewModel>() {
    override val viewModel: TransactionViewModel by activityViewModels()
    private val args: TransactionDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getByID(args.transaction.id)

        lifecycleScope.launchWhenCreated {
            viewModel.detailState.collect {
                when (it) {
                    DetailState.Loading -> {}
                    is DetailState.Success -> {
                        onDetailsLoaded(args.transaction)
                    }
                    DetailState.Empty -> {
                        findNavController().navigateUp()
                    }
                    else -> {}
                }
            }
        }
    }


    private fun onDetailsLoaded(transaction: Transaction) = with(binding.transactionDetails) {
        title.text = transaction.title
        amount.text = indianRupee(transaction.amount).cleanTextContent
        type.text = transaction.transactionType
        tag.text = transaction.date
        date.text = transaction.date
        note.text = transaction.note
        createdAt.text = transaction.createdAtDateFormat

        binding.editTransaction.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("transaction", transaction)
            }
            findNavController().navigate(
                R.id.action_transactionDetailsFragment_to_editTransactionFragment,
                bundle
            )
        }
    }

    override fun getViewBinding(
        inflator: LayoutInflater,
        container: ViewGroup?,
    ): FragmentTransactionDetailsBinding {
        return FragmentTransactionDetailsBinding.inflate(inflator, container, false)
    }
}