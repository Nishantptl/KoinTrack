package com.nishant.kointrack.data.mapper

import com.nishant.kointrack.data.local.ExchangeRateEntity
import com.nishant.kointrack.data.remote.ExchangeRateResponseDto
import com.nishant.kointrack.domain.model.ExchangeRate

fun ExchangeRateEntity.toDomain(): ExchangeRate {
    return ExchangeRate(
        baseCurrency = baseCurrency,
        targetCurrency = targetCurrency,
        rate = rate,
        lastUpdated = lastUpdated
    )
}

fun ExchangeRateResponseDto.toEntityList(): List<ExchangeRateEntity> {
    val base = baseCode ?: return emptyList()
    val timestamp = (timeLastUpdateUnix ?: (System.currentTimeMillis() / 1000)) * 1000
    return rates.map { (targetCurrency, rate) ->
        ExchangeRateEntity(
            baseCurrency = base,
            targetCurrency = targetCurrency,
            rate = rate,
            lastUpdated = timestamp
        )
    }
}
