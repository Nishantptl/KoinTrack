package com.nishant.kointrack.data.repository

import com.nishant.kointrack.data.local.TransactionDao
import com.nishant.kointrack.data.mapper.toDomain
import com.nishant.kointrack.data.mapper.toEntity
import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getTransactions(): Flow<List<Transaction>> {
        return transactionDao.getTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionById(id: String): Flow<Transaction?> {
        return transactionDao.getTransactionById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    override fun getTotalExpensesEUR(): Flow<Double> {
        return transactionDao.getTotalExpensesEUR().map { total ->
            total ?: 0.0
        }
    }
}
