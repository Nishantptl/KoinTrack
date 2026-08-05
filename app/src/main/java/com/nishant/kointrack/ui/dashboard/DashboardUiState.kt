package com.nishant.kointrack.ui.dashboard

import com.nishant.kointrack.domain.model.AnalyticsData
import com.nishant.kointrack.domain.model.BudgetStatus
import com.nishant.kointrack.domain.model.DashboardOverview
import com.nishant.kointrack.domain.model.Transaction

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(
        val overview: DashboardOverview,
        val budgetStatus: BudgetStatus,
        val analyticsData: AnalyticsData,
        val recentTransactions: List<Transaction>,
        val isBalanceVisible: Boolean = true
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}
