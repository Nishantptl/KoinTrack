package com.nishant.kointrack.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.kointrack.domain.repository.TransactionRepository
import com.nishant.kointrack.domain.usecase.GetAnalyticsDataUseCase
import com.nishant.kointrack.domain.usecase.GetBudgetStatusUseCase
import com.nishant.kointrack.domain.usecase.GetDashboardOverviewUseCase
import com.nishant.kointrack.domain.usecase.SetBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    getDashboardOverviewUseCase: GetDashboardOverviewUseCase,
    getBudgetStatusUseCase: GetBudgetStatusUseCase,
    getAnalyticsDataUseCase: GetAnalyticsDataUseCase,
    private val setBudgetUseCase: SetBudgetUseCase
) : ViewModel() {

    private val _isBalanceVisible = MutableStateFlow(true)

    val uiState: StateFlow<DashboardUiState> = combine(
        getDashboardOverviewUseCase(),
        getBudgetStatusUseCase(),
        getAnalyticsDataUseCase(),
        transactionRepository.getTransactions().map { it.take(5) },
        _isBalanceVisible
    ) { overview, budgetStatus, analyticsData, recentTransactions, isVisible ->
        DashboardUiState.Success(
            overview = overview,
            budgetStatus = budgetStatus,
            analyticsData = analyticsData,
            recentTransactions = recentTransactions,
            isBalanceVisible = isVisible
        ) as DashboardUiState
    }.catch { throwable ->
        emit(DashboardUiState.Error(throwable.localizedMessage ?: "Failed to load dashboard data."))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState.Loading
    )

    fun toggleBalanceVisibility() {
        _isBalanceVisible.value = !_isBalanceVisible.value
    }

    fun setBudgetLimit(limit: Double) {
        viewModelScope.launch {
            setBudgetUseCase(limit)
        }
    }

    fun deleteTransaction(transaction: com.nishant.kointrack.domain.model.Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }
}
