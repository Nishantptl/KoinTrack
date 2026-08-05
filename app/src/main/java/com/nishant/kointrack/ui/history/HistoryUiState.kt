package com.nishant.kointrack.ui.history

import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.model.TransactionFilter

sealed interface HistoryUiState {
    object Loading : HistoryUiState
    data class Success(
        val transactions: List<Transaction>,
        val filter: TransactionFilter
    ) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}
