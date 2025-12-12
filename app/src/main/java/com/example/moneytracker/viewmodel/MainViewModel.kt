package com.example.moneytracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.data.model.Budget
import com.example.moneytracker.data.model.FirestoreExpense
import com.example.moneytracker.data.model.Goal
import com.example.moneytracker.data.model.Income
import com.example.moneytracker.data.repository.AuthRepository
import com.example.moneytracker.data.repository.FirestoreRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class MainUiState(
    val budgets: List<Budget> = emptyList(),
    val expenses: List<FirestoreExpense> = emptyList(),
    val goals: List<Goal> = emptyList(),
    val incomes: List<Income> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class MainViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val firestoreRepository = FirestoreRepository()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val userId: String?
        get() = authRepository.getCurrentUserId()

    init {
        loadAllData()
    }

    fun loadAllData() {
        val uid = userId ?: return
        _uiState.value = _uiState.value.copy(isLoading = true)

        // Load budgets
        viewModelScope.launch {
            firestoreRepository.getBudgets(uid)
                .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.message) }
                .collect { budgets ->
                    _uiState.value = _uiState.value.copy(budgets = budgets, isLoading = false)
                }
        }

        // Load expenses
        viewModelScope.launch {
            firestoreRepository.getExpenses(uid)
                .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.message) }
                .collect { expenses ->
                    _uiState.value = _uiState.value.copy(expenses = expenses, isLoading = false)
                }
        }

        // Load goals
        viewModelScope.launch {
            firestoreRepository.getGoals(uid)
                .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.message) }
                .collect { goals ->
                    _uiState.value = _uiState.value.copy(goals = goals, isLoading = false)
                }
        }

        // Load incomes
        viewModelScope.launch {
            firestoreRepository.getIncomes(uid)
                .catch { e -> _uiState.value = _uiState.value.copy(errorMessage = e.message) }
                .collect { incomes ->
                    _uiState.value = _uiState.value.copy(incomes = incomes, isLoading = false)
                }
        }
    }

    // ==================== BUDGETS ====================
    fun addBudget(category: String, color: String, icon: String, limit: Double) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                val budget = Budget(
                    category = category,
                    color = color,
                    icon = icon,
                    limit = limit,
                    spent = 0.0,
                    userId = uid
                )
                firestoreRepository.addBudget(budget)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteBudget(budgetId: String) {
        viewModelScope.launch {
            try {
                firestoreRepository.deleteBudget(budgetId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    // ==================== EXPENSES ====================
    fun addExpense(amount: Double, category: String, description: String, date: Timestamp) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                val expense = FirestoreExpense(
                    amount = amount,
                    category = category,
                    description = description,
                    date = date,
                    userId = uid
                )
                firestoreRepository.addExpense(expense)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            try {
                firestoreRepository.deleteExpense(expenseId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    // ==================== GOALS ====================
    fun addGoal(title: String, category: String, description: String, color: String, priority: String, targetAmount: Double) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                val goal = Goal(
                    title = title,
                    category = category,
                    description = description,
                    color = color,
                    priority = priority,
                    targetAmount = targetAmount,
                    currentAmount = 0.0,
                    userId = uid
                )
                firestoreRepository.addGoal(goal)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun updateGoalAmount(goal: Goal, additionalAmount: Double) {
        viewModelScope.launch {
            try {
                val updatedGoal = goal.copy(currentAmount = goal.currentAmount + additionalAmount)
                firestoreRepository.updateGoal(updatedGoal)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            try {
                firestoreRepository.deleteGoal(goalId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    // ==================== INCOMES ====================
    fun addIncome(amount: Double, category: String, source: String, date: Timestamp) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                val income = Income(
                    amount = amount,
                    category = category,
                    source = source,
                    date = date,
                    userId = uid
                )
                firestoreRepository.addIncome(income)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteIncome(incomeId: String) {
        viewModelScope.launch {
            try {
                firestoreRepository.deleteIncome(incomeId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
