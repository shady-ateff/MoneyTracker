package com.example.moneytracker.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Income(
    @DocumentId
    val id: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val source: String = "",
    val date: Timestamp? = null,
    val createdAt: Timestamp? = null,
    val userId: String = ""
)
