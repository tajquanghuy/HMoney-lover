package com.financemanage.hmoneylover.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.financemanage.hmoneylover.model.Transaction
import com.financemanage.hmoneylover.repository.TransactionRepository
import com.financemanage.hmoneylover.resources.DetailState
import com.financemanage.hmoneylover.resources.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
) : ViewModel() {

    private val _transactionFilter = MutableStateFlow("Default")
    val transactionFilter: StateFlow<String> = _transactionFilter

    private val _uiState = MutableStateFlow<ViewState>(ViewState.Loading)
    private val _detailState = MutableStateFlow<DetailState>(DetailState.Loading)

    val uiState: StateFlow<ViewState> = _uiState
    val detailState: StateFlow<DetailState> = _detailState


    fun insertTransaction(transaction: Transaction) =
        viewModelScope.launch { repository.insert(transaction) }

    fun updateTransaction(transaction: Transaction) =
        viewModelScope.launch { repository.update(transaction) }

    fun deleteTransaction(transaction: Transaction) =
        viewModelScope.launch { repository.delete(transaction) }

    fun getAllTransaction(type: String) =
        viewModelScope.launch {
            repository.getAllSingleTransaction(type).collect {
                if (it.isNullOrEmpty()) {
                    _uiState.value = ViewState.Empty
                } else {
                    _uiState.value = ViewState.Success(it)
                }
            }
        }

    fun getByID(id: Int) =
        viewModelScope.launch {
            _detailState.value = DetailState.Loading
            repository.getByID(id).collect {
                if (it != null) {
                    _detailState.value = DetailState.Success(it)
                }
            }
        }

    fun allIncome() {
        _transactionFilter.value = "Income"
    }

    fun allExpense() {
        _transactionFilter.value = "Expense"
    }

    fun overall() {
        _transactionFilter.value = "Overall"
    }
}