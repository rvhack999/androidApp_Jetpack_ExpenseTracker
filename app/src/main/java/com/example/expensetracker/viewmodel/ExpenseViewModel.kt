package com.example.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    // ====== СОСТОЯНИЯ ======

    // Все расходы
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    // Планируемые расходы
    private val _plannedExpenses = MutableStateFlow<List<Expense>>(emptyList())
    val plannedExpenses: StateFlow<List<Expense>> = _plannedExpenses.asStateFlow()

    // Выбранная категория (для фильтрации)
    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    // Общая сумма за период
    private val _totalSum = MutableStateFlow<Long?>(null)
    val totalSum: StateFlow<Long?> = _totalSum.asStateFlow()

    // Состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ====== ИНИЦИАЛИЗАЦИЯ ======

    init {
        loadAllExpenses()
        loadPlannedExpenses()
    }

    // ====== ЗАГРУЗКА ======

    private fun loadAllExpenses() {
        viewModelScope.launch {
            repository.getAllExpenses().collect { expenses ->
                _expenses.value = expenses
            }
        }
    }

    private fun loadPlannedExpenses() {
        viewModelScope.launch {
            repository.getPlannedExpenses().collect { expenses ->
                _plannedExpenses.value = expenses
            }
        }
    }

    // ====== ДЕЙСТВИЯ ======

    // Загрузить расходы по категории
    fun loadExpensesByCategory(categoryId: Int) {
        viewModelScope.launch {
            _selectedCategoryId.value = categoryId
            repository.getExpensesByCategory(categoryId).collect { expenses ->
                _expenses.value = expenses
            }
        }
    }

    // Загрузить сумму за период
    fun loadTotalForPeriod(startDate: String, endDate: String) {
        viewModelScope.launch {
            repository.getTotalForPeriod(startDate, endDate).collect { sum ->
                _totalSum.value = sum
            }
        }
    }

    // Добавить расход
    fun addExpense(
        amount: Long,
        categoryId: Int?,
        description: String,
        isPlanned: Boolean
    ) {
        viewModelScope.launch {
            try {
                repository.addExpense(amount, categoryId, description, isPlanned)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка добавления: ${e.message}"
            }
        }
    }

    // Обновить расход
    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repository.updateExpense(expense)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка обновления: ${e.message}"
            }
        }
    }

    // Удалить расход
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repository.deleteExpense(expense)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка удаления: ${e.message}"
            }
        }
    }

    // Сбросить фильтр
    fun clearFilter() {
        _selectedCategoryId.value = null
        loadAllExpenses()
    }

    // Очистить ошибку
    fun clearError() {
        _error.value = null
    }
}