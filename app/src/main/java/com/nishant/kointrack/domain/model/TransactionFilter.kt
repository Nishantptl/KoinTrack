package com.nishant.kointrack.domain.model

enum class DateRangeFilter {
    ALL,
    THIS_WEEK,
    THIS_MONTH,
    LAST_30_DAYS
}

enum class SortOption {
    DATE_DESC,
    DATE_ASC,
    AMOUNT_DESC,
    AMOUNT_ASC
}

data class TransactionFilter(
    val query: String = "",
    val category: TransactionCategory? = null,
    val type: TransactionType? = null,
    val dateRange: DateRangeFilter = DateRangeFilter.ALL,
    val sortBy: SortOption = SortOption.DATE_DESC
)
