package com.expensetracker.app.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.app.R
import com.expensetracker.app.model.Category
import java.util.Locale

data class CategoryProgressItem(
    val category: Category,
    val spent: Double,
    val budget: Double
)

class CategoryBudgetAdapter(
    private var items: List<CategoryProgressItem>,
    private var isPrivacyEnabled: Boolean
) : RecyclerView.Adapter<CategoryBudgetAdapter.ViewHolder>() {

    fun updateData(newItems: List<CategoryProgressItem>, privacy: Boolean) {
        this.items = newItems
        this.isPrivacyEnabled = privacy
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_budget, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], isPrivacyEnabled)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvIcon: TextView = itemView.findViewById(R.id.tvBudgetCategoryIcon)
        private val tvName: TextView = itemView.findViewById(R.id.tvBudgetCategoryName)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvBudgetCategoryAmount)
        private val tvPercent: TextView = itemView.findViewById(R.id.tvBudgetPercent)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.pbBudgetCategory)

        fun bind(item: CategoryProgressItem, privacy: Boolean) {
            tvIcon.text = item.category.iconName
            tvName.text = item.category.name

            val pct = if (item.budget > 0) ((item.spent / item.budget) * 100).toInt() else 0
            progressBar.progress = pct.coerceIn(0, 100)

            if (privacy) {
                tvAmount.text = "•••• / ••••"
                tvPercent.text = "$pct%"
            } else {
                tvAmount.text = String.format(Locale.getDefault(), "₹%.0f / ₹%.0f", item.spent, item.budget)
                tvPercent.text = "$pct%"
            }

            // Color coding according to pacing: Green < 70%, Amber 70-99%, Rose >= 100%
            if (pct >= 100) {
                tvPercent.setTextColor(Color.parseColor("#FF5252"))
            } else if (pct >= 75) {
                tvPercent.setTextColor(Color.parseColor("#FFB300"))
            } else {
                tvPercent.setTextColor(Color.parseColor("#00E676"))
            }
        }
    }
}
