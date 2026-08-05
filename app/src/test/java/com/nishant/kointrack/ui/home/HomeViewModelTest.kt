package com.nishant.kointrack.ui.home

import com.nishant.kointrack.domain.model.ExchangeRate
import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.model.TransactionCategory
import com.nishant.kointrack.domain.model.TransactionType
import com.nishant.kointrack.domain.repository.ExchangeRateRepository
import com.nishant.kointrack.domain.repository.TransactionRepository
import com.nishant.kointrack.domain.usecase.GetMonthlyExpensesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeTransactionRepository : TransactionRepository {
        val list = mutableListOf(
            Transaction(
                id = "1",
                title = "Coffee",
                amount = 4.0,
                currency = "EUR",
                convertedAmountEUR = 4.0,
                category = TransactionCategory.FOOD,
                type = TransactionType.EXPENSE,
                timestamp = System.currentTimeMillis()
            )
        )
        override fun getTransactions(): Flow<List<Transaction>> = flowOf(list)
        override fun getTransactionById(id: String): Flow<Transaction?> = flowOf(list.find { it.id == id })
        override suspend fun insertTransaction(transaction: Transaction) { list.add(transaction) }
        override suspend fun deleteTransaction(transaction: Transaction) { list.remove(transaction) }
        override fun getTotalExpensesEUR(): Flow<Double> = flowOf(list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.convertedAmountEUR })
    }

    private class FakeExchangeRateRepository : ExchangeRateRepository {
        override fun getExchangeRates(base: String): Flow<List<ExchangeRate>> = flowOf(emptyList())
        override suspend fun fetchAndCacheLatestRates(base: String): Result<Unit> = Result.success(Unit)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits Success when data is available`() = runTest {
        val transactionRepo = FakeTransactionRepository()
        val exchangeRepo = FakeExchangeRateRepository()
        val getMonthlyExpensesUseCase = GetMonthlyExpensesUseCase(transactionRepo)

        val viewModel = HomeViewModel(transactionRepo, getMonthlyExpensesUseCase, exchangeRepo)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first { it is HomeUiState.Success }
        assertTrue(state is HomeUiState.Success)
        val successState = state as HomeUiState.Success
        assertEquals(1, successState.transactions.size)
        assertEquals(4.0, successState.totalExpensesEUR, 0.001)
    }
}
