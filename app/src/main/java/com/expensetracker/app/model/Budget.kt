package com.expensetracker.app.model

data class Budget(
    val categoryId: String,
    val monthlyLimit: Double,
    val monthYear: String // e.g. "2026-10"
)
