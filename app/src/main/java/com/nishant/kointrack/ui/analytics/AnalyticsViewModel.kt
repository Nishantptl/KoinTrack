package com.nishant.kointrack.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.kointrack.domain.usecase.GetAnalyticsDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    getAnalyticsDataUseCase: GetAnalyticsDataUseCase
) : ViewModel() {

    val uiState: StateFlow<AnalyticsUiState> = getAnalyticsDataUseCase()
        .map { analyticsData ->
            AnalyticsUiState.Success(analyticsData) as AnalyticsUiState
        }
        .catch { throwable ->
            emit(AnalyticsUiState.Error(throwable.localizedMessage ?: "Failed to load analytics."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AnalyticsUiState.Loading
        )
}
