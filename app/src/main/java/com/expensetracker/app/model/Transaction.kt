package com.expensetracker.app.model

import java.util.UUID

enum class TransactionType {
    EXPENSE,
    INCOME,
    TRANSFER
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: String,
    val type: TransactionType,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "",
    val account: String = "Main Account"
)
