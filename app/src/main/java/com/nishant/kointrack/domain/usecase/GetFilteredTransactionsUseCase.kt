package com.nishant.kointrack.domain.usecase

import com.nishant.kointrack.domain.model.DateRangeFilter
import com.nishant.kointrack.domain.model.SortOption
import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.model.TransactionFilter
import com.nishant.kointrack.domain.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetFilteredTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(filterFlow: Flow<TransactionFilter>): Flow<List<Transaction>> {
        return filterFlow.flatMapLatest { filter ->
            repository.getTransactions().map { transactions ->
                filterTransactions(transactions, filter)
            }
        }
    }

    private fun filterTransactions(
        transactions: List<Transaction>,
        filter: TransactionFilter
    ): List<Transaction> {
        return transactions.filter { tx ->
            val matchesQuery = filter.query.isBlank() ||
                    tx.title.contains(filter.query, ignoreCase = true) ||
                    (tx.note?.contains(filter.query, ignoreCase = true) == true)

            val matchesCategory = filter.category == null || tx.category == filter.category

            val matchesType = filter.type == null || tx.type == filter.type

            val matchesDateRange = when (filter.dateRange) {
                DateRangeFilter.ALL -> true
                DateRangeFilter.THIS_WEEK -> {
                    val weekAgo = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -7)
                    }.timeInMillis
                    tx.timestamp >= weekAgo
                }
                DateRangeFilter.THIS_MONTH -> {
                    val startOfMonth = Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    tx.timestamp >= startOfMonth
                }
                DateRangeFilter.LAST_30_DAYS -> {
                    val thirtyDaysAgo = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -30)
                    }.timeInMillis
                    tx.timestamp >= thirtyDaysAgo
                }
            }

            matchesQuery && matchesCategory && matchesType && matchesDateRange
        }.sortedWith { t1, t2 ->
            when (filter.sortBy) {
                SortOption.DATE_DESC -> t2.timestamp.compareTo(t1.timestamp)
                SortOption.DATE_ASC -> t1.timestamp.compareTo(t2.timestamp)
                SortOption.AMOUNT_DESC -> t2.amount.compareTo(t1.amount)
                SortOption.AMOUNT_ASC -> t1.amount.compareTo(t2.amount)
            }
        }
    }
}
