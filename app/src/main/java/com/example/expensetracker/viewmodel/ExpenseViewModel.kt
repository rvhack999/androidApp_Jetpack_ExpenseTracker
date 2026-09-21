package com.example.expensetracker.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _plannedTotalSum = MutableStateFlow<Long?>(null)
    val plannedTotalSum: StateFlow<Long?> = _plannedTotalSum.asStateFlow()
    private val categoryId: Int = savedStateHandle["categoryId"] ?: 0

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

        loadExpensesByCategoryInternal(categoryId)
        loadPlannedExpensesInternal(categoryId)

        val now = java.time.LocalDate.now()
        val startOfMonth = now.withDayOfMonth(1).toString() + "T00:00:00"
        val endOfMonth = now.toString() + "T23:59:59"

        loadTotalForCategoryInternal(categoryId, startOfMonth, endOfMonth)
        loadPlannedTotalForCategoryInternal(categoryId, startOfMonth, endOfMonth)
    }


    private fun loadTotalForCategory(categoryId: Int, startDate: String, endDate: String) {
        viewModelScope.launch {
            repository.getTotalForCategoryAndPeriod(categoryId, startDate, endDate).collect { sum ->
                _totalSum.value = sum
            }
        }
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
            repository.getPlannedExpensesByCategory(categoryId).collect { expenses ->
                _plannedExpenses.value = expenses
            }
        }
    }

    // ====== ДЕЙСТВИЯ ======

    // Загрузить расходы по категории
    fun loadExpensesByCategory(categoryId: Int) {
        loadExpensesByCategoryInternal(categoryId)
    }

    private fun loadPlannedExpensesInternal(categoryId: Int) {
        viewModelScope.launch {
            val flow = if (categoryId == -1) {
                repository.getUncategorizedPlannedExpenses()
            } else {
                repository.getPlannedExpensesByCategory(categoryId)
            }
            flow.collect { expenses ->
                _plannedExpenses.value = expenses
            }
        }
    }

    private fun loadTotalForCategoryInternal(categoryId: Int, startDate: String, endDate: String) {
        viewModelScope.launch {
            val flow = if (categoryId == -1) {
                repository.getUncategorizedTotalForPeriod(startDate, endDate)
            } else {
                repository.getTotalForCategoryAndPeriod(categoryId, startDate, endDate)
            }
            flow.collect { sum ->
                _totalSum.value = sum
            }
        }
    }

    private fun loadPlannedTotalForCategoryInternal(categoryId: Int, startDate: String, endDate: String) {
        viewModelScope.launch {
            val flow = if (categoryId == -1) {
                repository.getUncategorizedPlannedTotalForPeriod(startDate, endDate)
            } else {
                repository.getPlannedTotalForCategoryAndPeriod(categoryId, startDate, endDate)
            }
            flow.collect { sum ->
                _plannedTotalSum.value = sum
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

    fun clearExpenses() {
        _expenses.value = emptyList()
    }

    // Переключатель "Показать планируемые"
    private val _showPlanned = MutableStateFlow(false)
    val showPlanned: StateFlow<Boolean> = _showPlanned.asStateFlow()

    // Переключить режим
    fun toggleShowPlanned() {
        _showPlanned.value = !_showPlanned.value
        // Сумма уже загружается в init, ничего не нужно
    }

    fun setShowPlanned(value: Boolean) {
        _showPlanned.value = value
    }

    private fun loadPlannedTotalForCategory(categoryId: Int, startDate: String, endDate: String) {
        viewModelScope.launch {
            repository.getPlannedTotalForCategoryAndPeriod(categoryId, startDate, endDate).collect { sum ->
                _plannedTotalSum.value = sum
            }
        }
    }

    // Текущий редактируемый расход
    private val _currentExpense = MutableStateFlow<Expense?>(null)
    val currentExpense: StateFlow<Expense?> = _currentExpense.asStateFlow()

    fun setCurrentExpense(expense: Expense?) {
        _currentExpense.value = expense
    }

    private fun loadExpensesByCategoryInternal(categoryId: Int) {
        viewModelScope.launch {
            val flow = if (categoryId == -1) {
                repository.getUncategorizedExpenses()
            } else {
                repository.getExpensesByCategory(categoryId)
            }
            flow.collect { expenses ->
                _expenses.value = expenses
                _isLoading.value = false
            }
        }
    }
}