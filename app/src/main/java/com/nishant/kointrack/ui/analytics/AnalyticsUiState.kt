package com.nishant.kointrack.ui.analytics

import com.nishant.kointrack.domain.model.AnalyticsData

sealed interface AnalyticsUiState {
    object Loading : AnalyticsUiState
    data class Success(val data: AnalyticsData) : AnalyticsUiState
    data class Error(val message: String) : AnalyticsUiState
}
