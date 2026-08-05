package com.nishant.kointrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :base")
    fun getExchangeRates(base: String): Flow<List<ExchangeRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(rates: List<ExchangeRateEntity>)
}
