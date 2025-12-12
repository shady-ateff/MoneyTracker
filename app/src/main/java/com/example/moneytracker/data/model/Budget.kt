package com.example.moneytracker.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Budget(
    @DocumentId
    val id: String = "",
    val category: String = "",
    val color: String = "#9333ea",
    val icon: String = "",
    val limit: Double = 0.0,
    val spent: Double = 0.0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val userId: String = ""
)
