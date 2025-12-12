package com.example.moneytracker.data.repository

import android.util.Log
import com.example.moneytracker.data.model.Budget
import com.example.moneytracker.data.model.FirestoreExpense
import com.example.moneytracker.data.model.Goal
import com.example.moneytracker.data.model.Income
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG = "FirestoreRepository"

    // ==================== BUDGETS ====================
    fun getBudgets(userId: String): Flow<List<Budget>> = callbackFlow {
        Log.d(TAG, "Getting budgets for userId: $userId")
        val listener = firestore.collection("budgets")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error getting budgets: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val budgets = snapshot?.toObjects(Budget::class.java) ?: emptyList()
                Log.d(TAG, "Got ${budgets.size} budgets")
                trySend(budgets)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addBudget(budget: Budget): String {
        val docRef = firestore.collection("budgets").document()
        val budgetWithId = budget.copy(
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )
        docRef.set(budgetWithId).await()
        return docRef.id
    }

    suspend fun updateBudget(budget: Budget) {
        val updates = mapOf(
            "category" to budget.category,
            "color" to budget.color,
            "icon" to budget.icon,
            "limit" to budget.limit,
            "spent" to budget.spent,
            "updatedAt" to Timestamp.now()
        )
        firestore.collection("budgets").document(budget.id).update(updates).await()
    }

    suspend fun deleteBudget(budgetId: String) {
        firestore.collection("budgets").document(budgetId).delete().await()
    }

    // ==================== EXPENSES ====================
    fun getExpenses(userId: String): Flow<List<FirestoreExpense>> = callbackFlow {
        Log.d(TAG, "Getting expenses for userId: $userId")
        val listener = firestore.collection("expenses")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error getting expenses: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val expenses = snapshot?.toObjects(FirestoreExpense::class.java) ?: emptyList()
                Log.d(TAG, "Got ${expenses.size} expenses")
                trySend(expenses)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addExpense(expense: FirestoreExpense): String {
        val expenseRef = firestore.collection("expenses").document()
        val expenseWithTimestamp = expense.copy(
            createdAt = Timestamp.now()
        )

        // Find matching budget first
        val budgetQuery = firestore.collection("budgets")
            .whereEqualTo("userId", expense.userId)
            .whereEqualTo("category", expense.category)
            .get()
            .await()

        if (budgetQuery.isEmpty) {
            // No budget found, just add expense
            expenseRef.set(expenseWithTimestamp).await()
        } else {
            // Budget found, update transactionally
            val budgetDoc = budgetQuery.documents.first()
            firestore.runTransaction { transaction ->
                // Add expense
                transaction.set(expenseRef, expenseWithTimestamp)

                // Update budget spent
                val newSpent = (budgetDoc.getDouble("spent") ?: 0.0) + expense.amount
                transaction.update(budgetDoc.reference, "spent", newSpent)
                transaction.update(budgetDoc.reference, "updatedAt", Timestamp.now())
            }.await()
        }
        
        return expenseRef.id
    }

    suspend fun deleteExpense(expenseId: String) {
        val expenseRef = firestore.collection("expenses").document(expenseId)
        
        // Get expense data to know amount and category
        val expenseSnapshot = expenseRef.get().await()
        if (!expenseSnapshot.exists()) return

        val expense = expenseSnapshot.toObject(FirestoreExpense::class.java) ?: return

        // Find matching budget
        val budgetQuery = firestore.collection("budgets")
            .whereEqualTo("userId", expense.userId)
            .whereEqualTo("category", expense.category)
            .get()
            .await()

        if (budgetQuery.isEmpty) {
            expenseRef.delete().await()
        } else {
            val budgetDoc = budgetQuery.documents.first()
            firestore.runTransaction { transaction ->
                // Delete expense
                transaction.delete(expenseRef)

                // Decrease budget spent
                val currentSpent = budgetDoc.getDouble("spent") ?: 0.0
                val newSpent = (currentSpent - expense.amount).coerceAtLeast(0.0)
                transaction.update(budgetDoc.reference, "spent", newSpent)
                transaction.update(budgetDoc.reference, "updatedAt", Timestamp.now())
            }.await()
        }
    }

    // ==================== GOALS ====================
    fun getGoals(userId: String): Flow<List<Goal>> = callbackFlow {
        Log.d(TAG, "Getting goals for userId: $userId")
        val listener = firestore.collection("goals")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error getting goals: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val goals = snapshot?.toObjects(Goal::class.java) ?: emptyList()
                Log.d(TAG, "Got ${goals.size} goals")
                trySend(goals)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addGoal(goal: Goal): String {
        val docRef = firestore.collection("goals").document()
        val goalWithTimestamp = goal.copy(
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )
        docRef.set(goalWithTimestamp).await()
        return docRef.id
    }

    suspend fun updateGoal(goal: Goal) {
        val updates = mapOf(
            "title" to goal.title,
            "category" to goal.category,
            "description" to goal.description,
            "color" to goal.color,
            "priority" to goal.priority,
            "targetAmount" to goal.targetAmount,
            "currentAmount" to goal.currentAmount,
            "updatedAt" to Timestamp.now()
        )
        firestore.collection("goals").document(goal.id).update(updates).await()
    }

    suspend fun deleteGoal(goalId: String) {
        firestore.collection("goals").document(goalId).delete().await()
    }

    // ==================== INCOMES ====================
    fun getIncomes(userId: String): Flow<List<Income>> = callbackFlow {
        Log.d(TAG, "Getting incomes for userId: $userId")
        val listener = firestore.collection("incomes")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error getting incomes: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val incomes = snapshot?.toObjects(Income::class.java) ?: emptyList()
                Log.d(TAG, "Got ${incomes.size} incomes")
                trySend(incomes)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addIncome(income: Income): String {
        val docRef = firestore.collection("incomes").document()
        val incomeWithTimestamp = income.copy(
            createdAt = Timestamp.now()
        )
        docRef.set(incomeWithTimestamp).await()
        return docRef.id
    }

    suspend fun deleteIncome(incomeId: String) {
        firestore.collection("incomes").document(incomeId).delete().await()
    }
}
