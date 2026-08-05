package com.nishant.kointrack.ui.add_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.model.TransactionCategory
import com.nishant.kointrack.domain.model.TransactionType
import com.nishant.kointrack.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    fun onTitleChanged(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, errorMessage = null) }
    }

    fun onAmountChanged(newAmount: String) {
        _uiState.update { it.copy(amount = newAmount, errorMessage = null) }
    }

    fun onCurrencySelected(currency: String) {
        _uiState.update { it.copy(selectedCurrency = currency) }
    }

    fun onCategorySelected(category: TransactionCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onTypeSelected(type: TransactionType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun onNoteChanged(newNote: String) {
        _uiState.update { it.copy(note = newNote) }
    }

    fun saveTransaction() {
        val currentState = _uiState.value
        val amountValue = currentState.amount.toDoubleOrNull()

        if (currentState.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Title cannot be empty.") }
            return
        }

        if (amountValue == null || amountValue <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid positive amount.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val newTransaction = Transaction(
                title = currentState.title.trim(),
                amount = amountValue,
                currency = currentState.selectedCurrency,
                convertedAmountEUR = 0.0,
                category = currentState.selectedCategory,
                type = currentState.selectedType,
                timestamp = System.currentTimeMillis(),
                note = currentState.note.ifBlank { null }
            )

            val result = addTransactionUseCase(newTransaction)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to save transaction."
                    )
                }
            }
        }
    }
}
