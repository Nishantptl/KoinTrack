package com.nishant.kointrack.domain.usecase

import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.model.TransactionCategory
import com.nishant.kointrack.domain.model.TransactionType
import com.nishant.kointrack.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class GetMonthlyExpensesUseCaseTest {

    private class FakeTransactionRepository(
        private val transactions: List<Transaction>
    ) : TransactionRepository {
        override fun getTransactions(): Flow<List<Transaction>> = flowOf(transactions)
        override fun getTransactionById(id: String): Flow<Transaction?> = flowOf(transactions.find { it.id == id })
        override suspend fun insertTransaction(transaction: Transaction) {}
        override suspend fun deleteTransaction(transaction: Transaction) {}
        override fun getTotalExpensesEUR(): Flow<Double> = flowOf(transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.convertedAmountEUR })
    }

    @Test
    fun `invoke aggregates expenses for target month and excludes income and other months`() = runTest {
        val now = ZonedDateTime.of(2026, 8, 5, 12, 0, 0, 0, ZoneId.systemDefault())
        val currentMonthTime = now.toInstant().toEpochMilli()
        val previousMonthTime = now.minusMonths(1).toInstant().toEpochMilli()

        val transactions = listOf(
            Transaction(
                title = "Current Month Food Expense",
                amount = 50.0,
                currency = "EUR",
                convertedAmountEUR = 50.0,
                category = TransactionCategory.FOOD,
                type = TransactionType.EXPENSE,
                timestamp = currentMonthTime
            ),
            Transaction(
                title = "Current Month Utilities Expense",
                amount = 100.0,
                currency = "EUR",
                convertedAmountEUR = 100.0,
                category = TransactionCategory.UTILITIES,
                type = TransactionType.EXPENSE,
                timestamp = currentMonthTime
            ),
            Transaction(
                title = "Current Month Income",
                amount = 2000.0,
                currency = "EUR",
                convertedAmountEUR = 2000.0,
                category = TransactionCategory.INCOME,
                type = TransactionType.INCOME,
                timestamp = currentMonthTime
            ),
            Transaction(
                title = "Previous Month Expense",
                amount = 300.0,
                currency = "EUR",
                convertedAmountEUR = 300.0,
                category = TransactionCategory.HOUSING,
                type = TransactionType.EXPENSE,
                timestamp = previousMonthTime
            )
        )

        val repository = FakeTransactionRepository(transactions)
        val useCase = GetMonthlyExpensesUseCase(repository)

        val totalExpenses = useCase(referenceTimestamp = currentMonthTime).first()

        assertEquals(150.0, totalExpenses, 0.001)
    }
}
