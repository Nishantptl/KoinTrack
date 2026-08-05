package com.nishant.kointrack.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.repository.ExchangeRateRepository
import com.nishant.kointrack.domain.repository.TransactionRepository
import com.nishant.kointrack.domain.usecase.GetMonthlyExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val getMonthlyExpensesUseCase: GetMonthlyExpensesUseCase,
    private val exchangeRateRepository: ExchangeRateRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        transactionRepository.getTransactions(),
        transactionRepository.getTotalExpensesEUR(),
        getMonthlyExpensesUseCase()
    ) { transactions, totalExpenses, monthlyExpenses ->
        HomeUiState.Success(
            transactions = transactions,
            totalExpensesEUR = totalExpenses,
            monthlyExpensesEUR = monthlyExpenses
        ) as HomeUiState
    }.catch { throwable ->
        emit(HomeUiState.Error(throwable.localizedMessage ?: "An unexpected error occurred."))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    init {
        refreshExchangeRates()
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }

    fun refreshExchangeRates() {
        viewModelScope.launch {
            exchangeRateRepository.fetchAndCacheLatestRates("EUR")
        }
    }
}
