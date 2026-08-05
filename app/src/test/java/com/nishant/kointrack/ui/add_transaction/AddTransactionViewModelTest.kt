package com.nishant.kointrack.ui.add_transaction

import com.nishant.kointrack.domain.model.ExchangeRate
import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.model.TransactionCategory
import com.nishant.kointrack.domain.model.TransactionType
import com.nishant.kointrack.domain.repository.ExchangeRateRepository
import com.nishant.kointrack.domain.repository.TransactionRepository
import com.nishant.kointrack.domain.usecase.AddTransactionUseCase
import com.nishant.kointrack.domain.usecase.ConvertCurrencyUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddTransactionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeTransactionRepository : TransactionRepository {
        val list = mutableListOf<Transaction>()
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
    fun `saveTransaction with empty title sets errorMessage`() = runTest {
        val transactionRepo = FakeTransactionRepository()
        val exchangeRepo = FakeExchangeRateRepository()
        val convertUseCase = ConvertCurrencyUseCase(exchangeRepo)
        val addUseCase = AddTransactionUseCase(transactionRepo, convertUseCase)

        val viewModel = AddTransactionViewModel(addUseCase)
        viewModel.onTitleChanged("")
        viewModel.onAmountChanged("50.0")

        viewModel.saveTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `saveTransaction with valid input sets isSuccess to true`() = runTest {
        val transactionRepo = FakeTransactionRepository()
        val exchangeRepo = FakeExchangeRateRepository()
        val convertUseCase = ConvertCurrencyUseCase(exchangeRepo)
        val addUseCase = AddTransactionUseCase(transactionRepo, convertUseCase)

        val viewModel = AddTransactionViewModel(addUseCase)
        viewModel.onTitleChanged("Dinner")
        viewModel.onAmountChanged("25.5")
        viewModel.onCategorySelected(TransactionCategory.FOOD)

        viewModel.saveTransaction()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSuccess)
        assertEquals(1, transactionRepo.list.size)
    }
}
