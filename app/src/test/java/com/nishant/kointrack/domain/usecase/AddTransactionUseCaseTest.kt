package com.nishant.kointrack.domain.usecase

import com.nishant.kointrack.domain.model.ExchangeRate
import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.model.TransactionCategory
import com.nishant.kointrack.domain.model.TransactionType
import com.nishant.kointrack.domain.repository.ExchangeRateRepository
import com.nishant.kointrack.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddTransactionUseCaseTest {

    private class FakeTransactionRepository : TransactionRepository {
        val insertedTransactions = mutableListOf<Transaction>()

        override fun getTransactions(): Flow<List<Transaction>> = flowOf(insertedTransactions)
        override fun getTransactionById(id: String): Flow<Transaction?> = flowOf(insertedTransactions.find { it.id == id })
        override suspend fun insertTransaction(transaction: Transaction) {
            insertedTransactions.add(transaction)
        }
        override suspend fun deleteTransaction(transaction: Transaction) {
            insertedTransactions.remove(transaction)
        }
        override fun getTotalExpensesEUR(): Flow<Double> = flowOf(insertedTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.convertedAmountEUR })
    }

    private class FakeExchangeRateRepository : ExchangeRateRepository {
        override fun getExchangeRates(base: String): Flow<List<ExchangeRate>> {
            return flowOf(listOf(ExchangeRate("USD", "EUR", 0.90, System.currentTimeMillis())))
        }
        override suspend fun fetchAndCacheLatestRates(base: String): Result<Unit> = Result.success(Unit)
    }

    @Test
    fun `invoke with blank title returns failure`() = runTest {
        val transactionRepo = FakeTransactionRepository()
        val exchangeRepo = FakeExchangeRateRepository()
        val convertUseCase = ConvertCurrencyUseCase(exchangeRepo)
        val addUseCase = AddTransactionUseCase(transactionRepo, convertUseCase)

        val transaction = Transaction(
            title = "",
            amount = 10.0,
            currency = "EUR",
            convertedAmountEUR = 10.0,
            category = TransactionCategory.FOOD,
            type = TransactionType.EXPENSE,
            timestamp = System.currentTimeMillis()
        )

        val result = addUseCase(transaction)

        assertTrue(result.isFailure)
        assertEquals(0, transactionRepo.insertedTransactions.size)
    }

    @Test
    fun `invoke with non-positive amount returns failure`() = runTest {
        val transactionRepo = FakeTransactionRepository()
        val exchangeRepo = FakeExchangeRateRepository()
        val convertUseCase = ConvertCurrencyUseCase(exchangeRepo)
        val addUseCase = AddTransactionUseCase(transactionRepo, convertUseCase)

        val transaction = Transaction(
            title = "Coffee",
            amount = 0.0,
            currency = "EUR",
            convertedAmountEUR = 0.0,
            category = TransactionCategory.FOOD,
            type = TransactionType.EXPENSE,
            timestamp = System.currentTimeMillis()
        )

        val result = addUseCase(transaction)

        assertTrue(result.isFailure)
        assertEquals(0, transactionRepo.insertedTransactions.size)
    }

    @Test
    fun `invoke with valid transaction calculates EUR amount and saves transaction`() = runTest {
        val transactionRepo = FakeTransactionRepository()
        val exchangeRepo = FakeExchangeRateRepository()
        val convertUseCase = ConvertCurrencyUseCase(exchangeRepo)
        val addUseCase = AddTransactionUseCase(transactionRepo, convertUseCase)

        val transaction = Transaction(
            title = "Lunch",
            amount = 100.0,
            currency = "USD",
            convertedAmountEUR = 0.0,
            category = TransactionCategory.FOOD,
            type = TransactionType.EXPENSE,
            timestamp = System.currentTimeMillis()
        )

        val result = addUseCase(transaction)

        assertTrue(result.isSuccess)
        assertEquals(1, transactionRepo.insertedTransactions.size)
        assertEquals(90.0, transactionRepo.insertedTransactions[0].convertedAmountEUR, 0.001)
    }
}
