package com.nishant.kointrack.domain.usecase

import com.nishant.kointrack.domain.model.TransactionType
import com.nishant.kointrack.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import java.time.YearMonth
import javax.inject.Inject

class GetMonthlyExpensesUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(referenceTimestamp: Long = System.currentTimeMillis()): Flow<Double> {
        val targetYearMonth = getYearMonth(referenceTimestamp)
        return repository.getTransactions().map { transactions ->
            transactions
                .filter { transaction ->
                    transaction.type == TransactionType.EXPENSE &&
                            getYearMonth(transaction.timestamp) == targetYearMonth
                }
                .sumOf { it.convertedAmountEUR }
        }
    }

    private fun getYearMonth(timestamp: Long): YearMonth {
        return YearMonth.from(
            Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
        )
    }
}
