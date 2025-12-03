package com.example.moneytracker

import com.example.moneytracker.utils.ExpenseValidator
import org.junit.Test
import org.junit.Assert.*

class ExpenseTest {
    @Test
    fun validation_returnsTrue_forValidInput() {
        val isValid = ExpenseValidator.validateInput("Lunch", 15.0)
        assertTrue(isValid)
    }

    @Test
    fun validation_returnsFalse_forInvalidInput() {
        val isValid = ExpenseValidator.validateInput("", 15.0)
        assertFalse(isValid)
    }
    
    @Test
    fun validation_returnsFalse_forNegativeAmount() {
        val isValid = ExpenseValidator.validateInput("Lunch", -5.0)
        assertFalse(isValid)
    }
}
