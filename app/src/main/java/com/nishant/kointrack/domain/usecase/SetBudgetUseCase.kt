package com.nishant.kointrack.domain.usecase

import com.nishant.kointrack.domain.repository.BudgetRepository
import javax.inject.Inject

class SetBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(limit: Double) {
        if (limit > 0) {
            budgetRepository.setMonthlyBudgetLimit(limit)
        }
    }
}
