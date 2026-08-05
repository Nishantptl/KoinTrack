package com.nishant.kointrack.domain.repository

import com.nishant.kointrack.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<List<Transaction>>
    fun getTransactionById(id: String): Flow<Transaction?>
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    fun getTotalExpensesEUR(): Flow<Double>
}
