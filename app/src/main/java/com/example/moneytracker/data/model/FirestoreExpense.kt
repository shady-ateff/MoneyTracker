package com.example.moneytracker.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class FirestoreExpense(
    @DocumentId
    val id: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: Timestamp? = null,
    val description: String = "",
    val createdAt: Timestamp? = null,
    val userId: String = ""
)
