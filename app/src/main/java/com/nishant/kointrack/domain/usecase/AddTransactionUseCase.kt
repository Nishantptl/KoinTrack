package com.nishant.kointrack.domain.usecase

import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        if (transaction.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Transaction title cannot be blank."))
        }
        if (transaction.amount <= 0.0) {
            return Result.failure(IllegalArgumentException("Transaction amount must be greater than zero."))
        }

        return try {
            repository.insertTransaction(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
