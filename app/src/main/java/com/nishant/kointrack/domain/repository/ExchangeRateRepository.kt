package com.nishant.kointrack.domain.repository

import com.nishant.kointrack.domain.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

interface ExchangeRateRepository {
    fun getExchangeRates(base: String): Flow<List<ExchangeRate>>
    suspend fun fetchAndCacheLatestRates(base: String): Result<Unit>
}
