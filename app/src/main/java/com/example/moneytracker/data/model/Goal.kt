package com.example.moneytracker.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Goal(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val color: String = "#2563eb",
    val priority: String = "Medium",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val userId: String = ""
)
