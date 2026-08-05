package com.nishant.kointrack.domain.usecase

import com.nishant.kointrack.domain.model.AnalyticsData
import com.nishant.kointrack.domain.model.CategoryDistribution
import com.nishant.kointrack.domain.model.MonthlyTrend
import com.nishant.kointrack.domain.model.TopCategory
import com.nishant.kointrack.domain.model.TransactionType
import com.nishant.kointrack.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GetAnalyticsDataUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<AnalyticsData> {
        return repository.getTransactions().map { transactions ->
            val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
            val totalExpenseEUR = expenseTransactions.sumOf { it.amount }

            // 1. Category Spending Distribution
            val categoryTotals = expenseTransactions
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val categoryDistributions = categoryTotals.map { (cat, amount) ->
                val percentage = if (totalExpenseEUR > 0) ((amount / totalExpenseEUR) * 100).toFloat() else 0f
                CategoryDistribution(
                    category = cat,
                    totalEUR = amount,
                    percentage = percentage
                )
            }.sortedByDescending { it.totalEUR }

            // 2. Top 3 Spending Categories
            val topCategories = categoryDistributions.take(3).map { dist ->
                TopCategory(
                    category = dist.category,
                    totalEUR = dist.totalEUR,
                    percentage = dist.percentage
                )
            }

            // 3. 6-Month Monthly Expense Trend Data
            val monthlyTrends = mutableListOf<MonthlyTrend>()
            val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())

            for (i in 5 downTo 0) {
                val cal = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -i)
                }
                val year = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH)
                val label = monthFormat.format(cal.time)

                val monthExpenses = expenseTransactions.filter { tx ->
                    val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
                    txCal.get(Calendar.YEAR) == year && txCal.get(Calendar.MONTH) == month
                }.sumOf { it.amount }

                monthlyTrends.add(MonthlyTrend(monthLabel = label, totalEUR = monthExpenses))
            }

            AnalyticsData(
                categoryDistributions = categoryDistributions,
                monthlyTrends = monthlyTrends,
                topCategories = topCategories,
                totalExpenseEUR = totalExpenseEUR
            )
        }
    }
}
