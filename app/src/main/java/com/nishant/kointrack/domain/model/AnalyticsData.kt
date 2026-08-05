package com.nishant.kointrack.domain.model

data class CategoryDistribution(
    val category: TransactionCategory,
    val totalEUR: Double,
    val percentage: Float
)

data class MonthlyTrend(
    val monthLabel: String,
    val totalEUR: Double
)

data class TopCategory(
    val category: TransactionCategory,
    val totalEUR: Double,
    val percentage: Float
)

data class AnalyticsData(
    val categoryDistributions: List<CategoryDistribution>,
    val monthlyTrends: List<MonthlyTrend>,
    val topCategories: List<TopCategory>,
    val totalExpenseEUR: Double
)
