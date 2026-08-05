package com.nishant.kointrack.domain.model

enum class BudgetThreshold {
    GREEN,   // < 70%
    YELLOW,  // 70% - 90%
    RED      // > 90%
}

data class BudgetStatus(
    val limit: Double,
    val spent: Double,
    val remaining: Double,
    val percentageSpent: Float,
    val threshold: BudgetThreshold
)
