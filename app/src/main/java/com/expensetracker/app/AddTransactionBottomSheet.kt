package com.expensetracker.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.expensetracker.app.data.ExpenseRepository
import com.expensetracker.app.databinding.BottomSheetAddTransactionBinding
import com.expensetracker.app.model.Category
import com.expensetracker.app.model.Transaction
import com.expensetracker.app.model.TransactionType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class AddTransactionBottomSheet(
    private val onTransactionAdded: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddTransactionBinding? = null
    private val binding get() = _binding!!

    private var currentType: TransactionType = TransactionType.EXPENSE
    private var selectedCategory: Category? = null
    private var currentAmountStr: String = "0"
    private lateinit var repository: ExpenseRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = ExpenseRepository(requireContext())

        setupTypeSelector()
        setupCategories()
        setupNumpad()
        setupSaveButton()
    }

    private fun setupTypeSelector() {
        binding.toggleTypeGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnTypeExpense -> {
                        currentType = TransactionType.EXPENSE
                        binding.tvInputAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.expense_rose))
                    }
                    R.id.btnTypeIncome -> {
                        currentType = TransactionType.INCOME
                        binding.tvInputAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.income_green))
                    }
                    R.id.btnTypeTransfer -> {
                        currentType = TransactionType.TRANSFER
                        binding.tvInputAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.indigo_accent))
                    }
                }
                setupCategories()
            }
        }
    }

    private fun setupCategories() {
        binding.chipGroupCategories.removeAllViews()
        val allCategories = Category.getDefaultCategories()
        val filtered = when (currentType) {
            TransactionType.EXPENSE -> allCategories.filter { it.isExpense }
            TransactionType.INCOME -> allCategories.filter { !it.isExpense }
            TransactionType.TRANSFER -> allCategories
        }

        filtered.forEachIndexed { index, cat ->
            val chip = Chip(requireContext()).apply {
                text = "${cat.iconName} ${cat.name}"
                isCheckable = true
                isChecked = index == 0
                setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        selectedCategory = cat
                    }
                }
            }
            binding.chipGroupCategories.addView(chip)
            if (index == 0) {
                selectedCategory = cat
            }
        }
    }

    private fun setupNumpad() {
        val numberButtons = listOf(
            binding.btnNum0 to "0",
            binding.btnNum1 to "1",
            binding.btnNum2 to "2",
            binding.btnNum3 to "3",
            binding.btnNum4 to "4",
            binding.btnNum5 to "5",
            binding.btnNum6 to "6",
            binding.btnNum7 to "7",
            binding.btnNum8 to "8",
            binding.btnNum9 to "9",
            binding.btnNum00 to "00",
            binding.btnNumDot to "."
        )

        numberButtons.forEach { (button, digit) ->
            button.setOnClickListener {
                appendDigit(digit)
            }
        }

        binding.btnNumClear.setOnClickListener {
            currentAmountStr = "0"
            updateAmountDisplay()
        }

        binding.btnNumBackspace.setOnClickListener {
            if (currentAmountStr.length > 1) {
                currentAmountStr = currentAmountStr.dropLast(1)
            } else {
                currentAmountStr = "0"
            }
            updateAmountDisplay()
        }

        binding.btnNumPlus.setOnClickListener {
            // Calculator addition capability
            if (!currentAmountStr.endsWith("+") && !currentAmountStr.endsWith(".")) {
                currentAmountStr += "+"
                updateAmountDisplay()
            }
        }
    }

    private fun appendDigit(digit: String) {
        if (digit == ".") {
            val parts = currentAmountStr.split("+")
            val lastPart = parts.last()
            if (!lastPart.contains(".")) {
                currentAmountStr += "."
            }
        } else if (currentAmountStr == "0") {
            currentAmountStr = digit
        } else {
            currentAmountStr += digit
        }
        updateAmountDisplay()
    }

    private fun updateAmountDisplay() {
        binding.tvInputAmount.text = currentAmountStr
    }

    private fun calculateTotalAmount(): Double {
        return try {
            val parts = currentAmountStr.split("+")
            parts.filter { it.isNotBlank() }.sumOf { it.toDouble() }
        } catch (e: Exception) {
            0.0
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveTransaction.setOnClickListener {
            val amount = calculateTotalAmount()
            if (amount <= 0.0) {
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val titleInput = binding.etTransactionTitle.text?.toString()?.trim()
            val category = selectedCategory ?: Category.getDefaultCategories().first()
            val title = if (!titleInput.isNullOrBlank()) titleInput else category.name

            val transaction = Transaction(
                title = title,
                amount = amount,
                category = category.id,
                type = currentType,
                account = if (currentType == TransactionType.TRANSFER) "Savings" else "Main Account"
            )

            repository.addTransaction(transaction)
            Toast.makeText(requireContext(), "Saved ₹$amount successfully! 🚀", Toast.LENGTH_SHORT).show()
            onTransactionAdded()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
