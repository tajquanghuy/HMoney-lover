package com.financemanage.hmoneylover.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.financemanage.hmoneylover.model.Transaction
import com.financemanage.hmoneylover.utils.indianRupee
import com.financemanage.hmoneylover.R
import com.financemanage.hmoneylover.databinding.ItemTransactionLayoutBinding

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemTransactionLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding =
            ItemTransactionLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transactionsList = differ.currentList[position]
        holder.binding.apply {
            transactionName.text = transactionsList.title
            transactionCategory.text = transactionsList.tag

            when (transactionsList.transactionType) {
                "Earning" -> {
                    transactionAmount.setTextColor(
                        ContextCompat.getColor(
                            transactionAmount.context,
                            R.color.light_green
                        )
                    )
                    transactionAmount.text = "+ ".plus(indianRupee(transactionsList.amount))
                }
                "Spending" -> {
                    transactionAmount.setTextColor(
                        ContextCompat.getColor(
                            transactionAmount.context,
                            R.color.light_red
                        )
                    )
                    transactionAmount.text = "- ".plus(indianRupee(transactionsList.amount))
                }
            }

            when (transactionsList.tag) {
                "Salary" -> {
                    transactionIconView.setImageResource(R.drawable.ic_salary)
                }
                "Mutual funds returns" -> {
                    transactionIconView.setImageResource(R.drawable.ic_returns)
                }
                "Monthly Property Rent" -> {
                    transactionIconView.setImageResource(R.drawable.ic_property)
                }
                "Stock Market returns" -> {
                    transactionIconView.setImageResource(R.drawable.ic_returns)
                }
                "Other Income" -> {
                    transactionIconView.setImageResource(R.drawable.ic_others)
                }
                "Travel Expenses" -> {
                    transactionIconView.setImageResource(R.drawable.ic_travel)
                }
                "Healthcare Expenses" -> {
                    transactionIconView.setImageResource(R.drawable.ic_hospital)
                }
                "Food Expenses" -> {
                    transactionIconView.setImageResource(R.drawable.ic_food)
                }
                "Home Expenses" -> {
                    transactionIconView.setImageResource(R.drawable.ic_property)
                }
                "Entertainment Expenses" -> {
                    transactionIconView.setImageResource(R.drawable.ic_entertainment)
                }
                else -> {
                    transactionIconView.setImageResource(R.drawable.ic_others)
                }
            }

            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(transactionsList) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Transaction) -> Unit)? = null
    fun setOnItemClickListener(listener: (Transaction) -> Unit) {
        onItemClickListener = listener
    }
}