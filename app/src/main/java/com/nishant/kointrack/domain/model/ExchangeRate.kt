package com.nishant.kointrack.domain.model

data class ExchangeRate(
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
    val lastUpdated: Long
)
