package com.nishant.kointrack.domain.model

import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val currency: String,
    val convertedAmountEUR: Double,
    val category: TransactionCategory,
    val type: TransactionType,
    val timestamp: Long,
    val note: String? = null
)
