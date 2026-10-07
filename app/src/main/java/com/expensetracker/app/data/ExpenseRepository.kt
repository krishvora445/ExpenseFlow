package com.expensetracker.app.data

import android.content.Context
import android.content.SharedPreferences
import com.expensetracker.app.model.Budget
import com.expensetracker.app.model.Category
import com.expensetracker.app.model.Transaction
import com.expensetracker.app.model.TransactionType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class ExpenseRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("expense_tracker_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val KEY_TRANSACTIONS = "transactions_data"
    private val KEY_BUDGETS = "budgets_data"
    private val KEY_PRIVACY = "privacy_hidden"

    init {
        if (!prefs.contains(KEY_TRANSACTIONS)) {
            seedSampleData()
        }
    }

    fun getAllTransactions(): List<Transaction> {
        val json = prefs.getString(KEY_TRANSACTIONS, null) ?: return emptyList()
        val type = object : TypeToken<List<Transaction>>() {}.type
        val list: List<Transaction>? = gson.fromJson(json, type)
        return list?.sortedByDescending { it.timestamp } ?: emptyList()
    }

    fun addTransaction(transaction: Transaction) {
        val currentList = getAllTransactions().toMutableList()
        currentList.add(0, transaction)
        saveTransactions(currentList)
    }

    fun deleteTransaction(id: String) {
        val currentList = getAllTransactions().filterNot { it.id == id }
        saveTransactions(currentList)
    }

    private fun saveTransactions(list: List<Transaction>) {
        val json = gson.toJson(list)
        prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
    }

    fun getBudgets(): List<Budget> {
        val json = prefs.getString(KEY_BUDGETS, null) ?: return emptyList()
        val type = object : TypeToken<List<Budget>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun setBudget(categoryId: String, monthlyLimit: Double, monthYear: String) {
        val list = getBudgets().filterNot { it.categoryId == categoryId && it.monthYear == monthYear }.toMutableList()
        list.add(Budget(categoryId, monthlyLimit, monthYear))
        prefs.edit().putString(KEY_BUDGETS, gson.toJson(list)).apply()
    }

    fun isPrivacyEnabled(): Boolean {
        return prefs.getBoolean(KEY_PRIVACY, false)
    }

    fun setPrivacyEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PRIVACY, enabled).apply()
    }

    private fun seedSampleData() {
        val cal = Calendar.getInstance()
        val currentMonthYear = String.format("%d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)

        val samples = listOf(
            Transaction(
                title = "Grocery Store Restock",
                amount = 1450.0,
                category = "groceries",
                type = TransactionType.EXPENSE,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 3, // 3 hours ago
                note = "Organic produce & weekly dairy",
                account = "HDFC Bank"
            ),
            Transaction(
                title = "Client Project Milestone",
                amount = 25000.0,
                category = "freelance",
                type = TransactionType.INCOME,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24, // 1 day ago
                note = "Mobile app redesign handover",
                account = "ICICI Bank"
            ),
            Transaction(
                title = "Blue Tokai Coffee & Pastry",
                amount = 380.0,
                category = "food",
                type = TransactionType.EXPENSE,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 28,
                note = "Study session with classmates",
                account = "UPI / Paytm"
            ),
            Transaction(
                title = "Monthly High-Speed Fiber",
                amount = 999.0,
                category = "bills",
                type = TransactionType.EXPENSE,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 72,
                note = "Airtel Xstream 200Mbps plan",
                account = "Main Account"
            ),
            Transaction(
                title = "Cab to Tech Seminar",
                amount = 240.0,
                category = "transit",
                type = TransactionType.EXPENSE,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 96,
                note = "Uber ride to university campus",
                account = "UPI / Paytm"
            )
        )
        saveTransactions(samples)

        // Seed default category budgets
        val defaultBudgets = listOf(
            Budget("food", 6000.0, currentMonthYear),
            Budget("groceries", 8000.0, currentMonthYear),
            Budget("transit", 3000.0, currentMonthYear),
            Budget("bills", 5000.0, currentMonthYear),
            Budget("shopping", 4000.0, currentMonthYear)
        )
        prefs.edit().putString(KEY_BUDGETS, gson.toJson(defaultBudgets)).apply()
    }
}
