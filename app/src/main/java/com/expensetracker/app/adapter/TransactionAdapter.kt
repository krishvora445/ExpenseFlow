package com.expensetracker.app.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.app.R
import com.expensetracker.app.model.Category
import com.expensetracker.app.model.Transaction
import com.expensetracker.app.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private var isPrivacyEnabled: Boolean,
    private val onItemClick: (Transaction) -> Unit,
    private val onItemLongClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private val categoryMap = Category.getDefaultCategories().associateBy { it.id }
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

    fun updateData(newTransactions: List<Transaction>, privacy: Boolean) {
        this.transactions = newTransactions
        this.isPrivacyEnabled = privacy
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = transactions[position]
        holder.bind(item, isPrivacyEnabled, categoryMap[item.category], dateFormat, onItemClick, onItemLongClick)
    }

    override fun getItemCount(): Int = transactions.size

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTransactionTitle)
        private val tvCategoryDate: TextView = itemView.findViewById(R.id.tvCategoryAndDate)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        private val tvAccount: TextView = itemView.findViewById(R.id.tvAccountBadge)

        fun bind(
            item: Transaction,
            privacy: Boolean,
            category: Category?,
            dateFormat: SimpleDateFormat,
            onClick: (Transaction) -> Unit,
            onLongClick: (Transaction) -> Unit
        ) {
            tvIcon.text = category?.iconName ?: "💸"
            tvTitle.text = item.title
            
            val catName = category?.name ?: item.category.replaceFirstChar { it.uppercase() }
            val formattedDate = dateFormat.format(Date(item.timestamp))
            tvCategoryDate.text = "$catName • $formattedDate"
            
            tvAccount.text = item.account

            if (privacy) {
                tvAmount.text = "••••••"
                tvAmount.setTextColor(Color.parseColor("#94A3B8"))
            } else {
                when (item.type) {
                    TransactionType.EXPENSE -> {
                        tvAmount.text = String.format(Locale.getDefault(), "- ₹%.2f", item.amount)
                        tvAmount.setTextColor(Color.parseColor("#FF5252"))
                    }
                    TransactionType.INCOME -> {
                        tvAmount.text = String.format(Locale.getDefault(), "+ ₹%.2f", item.amount)
                        tvAmount.setTextColor(Color.parseColor("#00E676"))
                    }
                    TransactionType.TRANSFER -> {
                        tvAmount.text = String.format(Locale.getDefault(), "₹%.2f", item.amount)
                        tvAmount.setTextColor(Color.parseColor("#54A0FF"))
                    }
                }
            }

            itemView.setOnClickListener { onClick(item) }
            itemView.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }
}
