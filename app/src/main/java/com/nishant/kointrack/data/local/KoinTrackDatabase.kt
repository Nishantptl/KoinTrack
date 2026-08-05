package com.nishant.kointrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TransactionEntity::class, ExchangeRateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KoinTrackDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun exchangeRateDao(): ExchangeRateDao
}
