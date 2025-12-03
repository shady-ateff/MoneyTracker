package com.example.moneytracker.utils

object ExpenseValidator {
    fun validateInput(title: String, amount: Double): Boolean {
        return title.isNotBlank() && amount > 0
    }
}
