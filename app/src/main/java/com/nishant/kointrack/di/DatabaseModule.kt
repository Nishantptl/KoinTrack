package com.nishant.kointrack.di

import android.content.Context
import androidx.room.Room
import com.nishant.kointrack.data.local.KoinTrackDatabase
import com.nishant.kointrack.data.local.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideKoinTrackDatabase(
        @ApplicationContext context: Context
    ): KoinTrackDatabase {
        return Room.databaseBuilder(
            context,
            KoinTrackDatabase::class.java,
            "kointrack.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: KoinTrackDatabase): TransactionDao {
        return database.transactionDao()
    }
}
