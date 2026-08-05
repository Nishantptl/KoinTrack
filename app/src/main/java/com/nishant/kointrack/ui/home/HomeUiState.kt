package com.nishant.kointrack.ui.home

import com.nishant.kointrack.domain.model.Transaction

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val transactions: List<Transaction>,
        val totalExpensesEUR: Double,
        val monthlyExpensesEUR: Double
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
