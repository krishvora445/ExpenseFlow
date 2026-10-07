package com.expensetracker.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.app.adapter.CategoryBudgetAdapter
import com.expensetracker.app.adapter.CategoryProgressItem
import com.expensetracker.app.adapter.TransactionAdapter
import com.expensetracker.app.data.ExpenseRepository
import com.expensetracker.app.databinding.ActivityMainBinding
import com.expensetracker.app.model.Category
import com.expensetracker.app.model.Transaction
import com.expensetracker.app.model.TransactionType
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: ExpenseRepository
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var categoryBudgetAdapter: CategoryBudgetAdapter
    private var isPrivacyEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = ExpenseRepository(this)
        isPrivacyEnabled = repository.isPrivacyEnabled()

        setupRecyclerViews()
        setupListeners()
        refreshDashboardData()
    }

    private fun setupRecyclerViews() {
        // Transactions Recycler
        transactionAdapter = TransactionAdapter(
            transactions = emptyList(),
            isPrivacyEnabled = isPrivacyEnabled,
            onItemClick = { transaction ->
                Toast.makeText(this, "${transaction.title}: ${transaction.note.ifEmpty { "No note" }}", Toast.LENGTH_SHORT).show()
            },
            onItemLongClick = { transaction ->
                showDeleteConfirmDialog(transaction)
            }
        )
        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = transactionAdapter

        // Category Budget Progress Recycler
        categoryBudgetAdapter = CategoryBudgetAdapter(
            items = emptyList(),
            isPrivacyEnabled = isPrivacyEnabled
        )
        binding.rvCategoryBudgets.layoutManager = LinearLayoutManager(this)
        binding.rvCategoryBudgets.adapter = categoryBudgetAdapter
    }

    private fun setupListeners() {
        binding.btnTogglePrivacy.setOnClickListener {
            isPrivacyEnabled = !isPrivacyEnabled
            repository.setPrivacyEnabled(isPrivacyEnabled)
            updatePrivacyButtonText()
            refreshDashboardData()
        }

        binding.fabAddTransaction.setOnClickListener {
            val bottomSheet = AddTransactionBottomSheet {
                refreshDashboardData()
            }
            bottomSheet.show(supportFragmentManager, "AddTransactionBottomSheet")
        }
    }

    private fun updatePrivacyButtonText() {
        if (isPrivacyEnabled) {
            binding.btnTogglePrivacy.text = "🔒 Hidden"
        } else {
            binding.btnTogglePrivacy.text = "👁 Visible"
        }
    }

    private fun refreshDashboardData() {
        updatePrivacyButtonText()
        val allTransactions = repository.getAllTransactions()

        // Calculate Totals
        val totalIncome = allTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = allTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val totalBalance = totalIncome - totalExpense

        // Update Header Cards with Privacy Obfuscation
        if (isPrivacyEnabled) {
            binding.tvTotalBalance.text = "₹ ••••••••"
            binding.tvMonthlyIncome.text = "+ ₹ ••••"
            binding.tvMonthlyExpense.text = "- ₹ ••••"
        } else {
            binding.tvTotalBalance.text = String.format(Locale.getDefault(), "₹ %,.2f", totalBalance)
            binding.tvMonthlyIncome.text = String.format(Locale.getDefault(), "+ ₹ %,.2f", totalIncome)
            binding.tvMonthlyExpense.text = String.format(Locale.getDefault(), "- ₹ %,.2f", totalExpense)
        }

        binding.tvTransactionCount.text = "${allTransactions.size} entries"

        // Update Transactions List
        transactionAdapter.updateData(allTransactions, isPrivacyEnabled)

        // Calculate Category Budgets & Pacing
        val defaultBudgets = repository.getBudgets()
        val cal = Calendar.getInstance()
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val remainingDays = (daysInMonth - currentDay + 1).coerceAtLeast(1)

        val totalBudget = defaultBudgets.sumOf { it.monthlyLimit }
        val remainingBudget = (totalBudget - totalExpense).coerceAtLeast(0.0)
        val dailyAllowance = remainingBudget / remainingDays

        if (isPrivacyEnabled) {
            binding.tvDailyAllowance.text = "₹ •••/day"
        } else {
            binding.tvDailyAllowance.text = String.format(Locale.getDefault(), "₹ %.0f/day left", dailyAllowance)
        }

        val categoryMap = Category.getDefaultCategories().associateBy { it.id }
        val budgetProgressItems = defaultBudgets.map { budget ->
            val spentForCat = allTransactions
                .filter { it.category == budget.categoryId && it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
            val cat = categoryMap[budget.categoryId] ?: Category(budget.categoryId, budget.categoryId, "📁", "#6C5CE7")
            CategoryProgressItem(
                category = cat,
                spent = spentForCat,
                budget = budget.monthlyLimit
            )
        }

        categoryBudgetAdapter.updateData(budgetProgressItems, isPrivacyEnabled)
    }

    private fun showDeleteConfirmDialog(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Do you want to delete '${transaction.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                repository.deleteTransaction(transaction.id)
                refreshDashboardData()
                Toast.makeText(this, "Deleted '${transaction.title}'", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
