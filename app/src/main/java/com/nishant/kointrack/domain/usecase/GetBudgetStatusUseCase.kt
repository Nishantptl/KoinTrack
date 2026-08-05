package com.nishant.kointrack.domain.usecase

import com.nishant.kointrack.domain.model.BudgetStatus
import com.nishant.kointrack.domain.model.BudgetThreshold
import com.nishant.kointrack.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetBudgetStatusUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val getMonthlyExpensesUseCase: GetMonthlyExpensesUseCase
) {
    operator fun invoke(): Flow<BudgetStatus> {
        return combine(
            budgetRepository.getMonthlyBudgetLimit(),
            getMonthlyExpensesUseCase()
        ) { limit, spent ->
            val safeLimit = if (limit <= 0.0) 1.0 else limit
            val percentage = (spent / safeLimit).toFloat().coerceIn(0f, 2f)
            val remaining = limit - spent

            val threshold = when {
                percentage < 0.70f -> BudgetThreshold.GREEN
                percentage <= 0.90f -> BudgetThreshold.YELLOW
                else -> BudgetThreshold.RED
            }

            BudgetStatus(
                limit = limit,
                spent = spent,
                remaining = remaining,
                percentageSpent = percentage,
                threshold = threshold
            )
        }
    }
}
