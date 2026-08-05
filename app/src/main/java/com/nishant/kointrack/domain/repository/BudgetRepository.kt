package com.nishant.kointrack.domain.repository

import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getMonthlyBudgetLimit(): Flow<Double>
    suspend fun setMonthlyBudgetLimit(limit: Double)
}
