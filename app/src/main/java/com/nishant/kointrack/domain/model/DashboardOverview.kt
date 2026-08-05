package com.nishant.kointrack.domain.model

data class DailySpendPoint(
    val dayLabel: String,
    val amountEUR: Double
)

data class DashboardOverview(
    val totalBalanceEUR: Double,
    val totalIncomeEUR: Double,
    val totalExpenseEUR: Double,
    val weeklyCurvePoints: List<DailySpendPoint>
)
