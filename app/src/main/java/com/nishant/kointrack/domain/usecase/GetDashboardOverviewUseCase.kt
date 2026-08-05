package com.nishant.kointrack.domain.usecase

import com.nishant.kointrack.domain.model.DailySpendPoint
import com.nishant.kointrack.domain.model.DashboardOverview
import com.nishant.kointrack.domain.model.TransactionType
import com.nishant.kointrack.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GetDashboardOverviewUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<DashboardOverview> {
        return repository.getTransactions().map { transactions ->
            val incomeTotal = transactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }

            val expenseTotal = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            val netBalance = incomeTotal - expenseTotal

            val curvePoints = mutableListOf<DailySpendPoint>()
            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

            for (i in 6 downTo 0) {
                val cal = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -i)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val startOfDay = cal.timeInMillis
                val endOfDay = startOfDay + (24 * 60 * 60 * 1000) - 1
                val label = dayFormat.format(cal.time)

                val dayExpenseSum = transactions
                    .filter { it.type == TransactionType.EXPENSE && it.timestamp in startOfDay..endOfDay }
                    .sumOf { it.amount }

                curvePoints.add(DailySpendPoint(dayLabel = label, amountEUR = dayExpenseSum))
            }

            DashboardOverview(
                totalBalanceEUR = netBalance,
                totalIncomeEUR = incomeTotal,
                totalExpenseEUR = expenseTotal,
                weeklyCurvePoints = curvePoints
            )
        }
    }
}
