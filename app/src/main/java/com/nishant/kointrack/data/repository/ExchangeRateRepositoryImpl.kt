package com.nishant.kointrack.data.repository

import com.nishant.kointrack.data.local.ExchangeRateDao
import com.nishant.kointrack.data.mapper.toDomain
import com.nishant.kointrack.data.mapper.toEntityList
import com.nishant.kointrack.data.remote.ExchangeRateApi
import com.nishant.kointrack.domain.model.ExchangeRate
import com.nishant.kointrack.domain.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExchangeRateRepositoryImpl @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao,
    private val exchangeRateApi: ExchangeRateApi
) : ExchangeRateRepository {

    override fun getExchangeRates(base: String): Flow<List<ExchangeRate>> {
        return exchangeRateDao.getExchangeRates(base).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun fetchAndCacheLatestRates(base: String): Result<Unit> {
        return try {
            val responseDto = exchangeRateApi.getLatestRates(base)
            val entities = responseDto.toEntityList()
            if (entities.isNotEmpty()) {
                exchangeRateDao.insertExchangeRates(entities)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
