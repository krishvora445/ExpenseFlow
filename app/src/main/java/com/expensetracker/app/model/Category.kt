package com.expensetracker.app.model

data class Category(
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isExpense: Boolean = true
) {
    companion object {
        fun getDefaultCategories(): List<Category> = listOf(
            Category("food", "Food & Dining", "🍔", "#FF5252"),
            Category("transit", "Transportation", "🚗", "#54A0FF"),
            Category("bills", "Bills & Utilities", "💡", "#FFB300"),
            Category("shopping", "Shopping", "🛍️", "#A29BFE"),
            Category("entertainment", "Entertainment", "🎬", "#FF7675"),
            Category("groceries", "Groceries", "🛒", "#00D2D3"),
            Category("health", "Health & Fitness", "💊", "#1DD1A1"),
            Category("salary", "Salary", "💼", "#00E676", isExpense = false),
            Category("freelance", "Freelance", "💻", "#10AC84", isExpense = false),
            Category("investment", "Investment", "📈", "#5F27CD", isExpense = false)
        )
    }
}
