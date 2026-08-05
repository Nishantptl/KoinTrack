package com.nishant.kointrack.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.kointrack.domain.model.DateRangeFilter
import com.nishant.kointrack.domain.model.SortOption
import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.model.TransactionCategory
import com.nishant.kointrack.domain.model.TransactionFilter
import com.nishant.kointrack.domain.model.TransactionType
import com.nishant.kointrack.domain.repository.TransactionRepository
import com.nishant.kointrack.domain.usecase.GetFilteredTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    getFilteredTransactionsUseCase: GetFilteredTransactionsUseCase
) : ViewModel() {

    private val _filterState = MutableStateFlow(TransactionFilter())
    val filterState: StateFlow<TransactionFilter> = _filterState.asStateFlow()

    val uiState: StateFlow<HistoryUiState> = combine(
        getFilteredTransactionsUseCase(_filterState),
        _filterState
    ) { transactions, filter ->
        HistoryUiState.Success(
            transactions = transactions,
            filter = filter
        ) as HistoryUiState
    }.catch { throwable ->
        emit(HistoryUiState.Error(throwable.localizedMessage ?: "Failed to load transaction history."))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState.Loading
    )

    fun updateQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
    }

    fun updateCategory(category: TransactionCategory?) {
        _filterState.value = _filterState.value.copy(category = category)
    }

    fun updateType(type: TransactionType?) {
        _filterState.value = _filterState.value.copy(type = type)
    }

    fun updateDateRange(dateRange: DateRangeFilter) {
        _filterState.value = _filterState.value.copy(dateRange = dateRange)
    }

    fun updateSortBy(sortBy: SortOption) {
        _filterState.value = _filterState.value.copy(sortBy = sortBy)
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }
}
