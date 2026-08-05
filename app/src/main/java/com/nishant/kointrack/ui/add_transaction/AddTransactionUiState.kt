package com.nishant.kointrack.ui.add_transaction

import com.nishant.kointrack.domain.model.TransactionCategory
import com.nishant.kointrack.domain.model.TransactionType

data class AddTransactionUiState(
    val title: String = "",
    val amount: String = "",
    val selectedCurrency: String = "EUR",
    val selectedCategory: TransactionCategory = TransactionCategory.FOOD,
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val note: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
