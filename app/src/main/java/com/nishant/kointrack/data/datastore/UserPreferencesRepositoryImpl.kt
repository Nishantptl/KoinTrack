package com.nishant.kointrack.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.nishant.kointrack.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : BudgetRepository {

    private object PreferencesKeys {
        val MONTHLY_BUDGET_LIMIT = doublePreferencesKey("monthly_budget_limit")
    }

    override fun getMonthlyBudgetLimit(): Flow<Double> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.MONTHLY_BUDGET_LIMIT] ?: DEFAULT_BUDGET_LIMIT
            }
    }

    override suspend fun setMonthlyBudgetLimit(limit: Double) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MONTHLY_BUDGET_LIMIT] = limit
        }
    }

    companion object {
        const val DEFAULT_BUDGET_LIMIT = 1000.0
    }
}
