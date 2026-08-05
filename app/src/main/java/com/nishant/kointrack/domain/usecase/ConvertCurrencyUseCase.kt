package com.nishant.kointrack.domain.usecase

import com.nishant.kointrack.domain.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor(
    private val exchangeRateRepository: ExchangeRateRepository
) {
    suspend operator fun invoke(amount: Double, fromCurrency: String): Double {
        if (fromCurrency.equals("EUR", ignoreCase = true)) {
            return amount
        }

        val rates = exchangeRateRepository.getExchangeRates(fromCurrency).firstOrNull()
        val eurRate = rates?.find { it.targetCurrency.equals("EUR", ignoreCase = true) }?.rate

        return if (eurRate != null && eurRate > 0.0) {
            amount * eurRate
        } else {
            amount
        }
    }
}
