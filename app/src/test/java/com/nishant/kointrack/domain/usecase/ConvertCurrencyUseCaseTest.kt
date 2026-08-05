package com.nishant.kointrack.domain.usecase

import com.nishant.kointrack.domain.model.ExchangeRate
import com.nishant.kointrack.domain.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertCurrencyUseCaseTest {

    private class FakeExchangeRateRepository(
        private val ratesMap: Map<String, List<ExchangeRate>> = emptyMap()
    ) : ExchangeRateRepository {
        override fun getExchangeRates(base: String): Flow<List<ExchangeRate>> {
            return flowOf(ratesMap[base] ?: emptyList())
        }

        override suspend fun fetchAndCacheLatestRates(base: String): Result<Unit> {
            return Result.success(Unit)
        }
    }

    @Test
    fun `invoke with EUR currency returns original amount`() = runTest {
        val fakeRepo = FakeExchangeRateRepository()
        val useCase = ConvertCurrencyUseCase(fakeRepo)

        val result = useCase(amount = 100.0, fromCurrency = "EUR")

        assertEquals(100.0, result, 0.001)
    }

    @Test
    fun `invoke with USD currency converts amount to EUR when rate exists`() = runTest {
        val rates = listOf(
            ExchangeRate(baseCurrency = "USD", targetCurrency = "EUR", rate = 0.92, lastUpdated = System.currentTimeMillis())
        )
        val fakeRepo = FakeExchangeRateRepository(mapOf("USD" to rates))
        val useCase = ConvertCurrencyUseCase(fakeRepo)

        val result = useCase(amount = 100.0, fromCurrency = "USD")

        assertEquals(92.0, result, 0.001)
    }

    @Test
    fun `invoke with missing rate returns fallback original amount`() = runTest {
        val fakeRepo = FakeExchangeRateRepository()
        val useCase = ConvertCurrencyUseCase(fakeRepo)

        val result = useCase(amount = 50.0, fromCurrency = "GBP")

        assertEquals(50.0, result, 0.001)
    }
}
