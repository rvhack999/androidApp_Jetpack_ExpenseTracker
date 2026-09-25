package com.example.expensetracker.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Income
import com.example.expensetracker.data.repository.IncomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: IncomeRepository
): ViewModel()
{
    // ====== СОСТОЯНИЯ ======
    // Список доходов
    private val _income = MutableStateFlow<List<Income>>(emptyList())
    val income: StateFlow<List<Income>> = _income.asStateFlow()

    // Состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Выбранный пункт (для редактирования)
    private val _selectedIncome = MutableStateFlow<Income?>(null)
    val selectedIncome: StateFlow<Income?> = _selectedIncome.asStateFlow()

    // Активность
    private val _isActiveScreen = MutableStateFlow(true)
    val isActiveScreen: StateFlow<Boolean> = _isActiveScreen.asStateFlow()

    // ====== ЗАГРУЗКА ======

    private fun loadIncome(){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getAllIncome().collect { incomes ->
                    _income.value = incomes

                    // ИСПРАВЛЕНО: Явно используем sumOf { ... : Long } для предотвращения падения корутины
                    _totalSum.value = incomes.sumOf { it.amount }

                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // ====== ДЕЙСТВИЯ ======

    // Создать пункт дохода
    fun addIncome(name: String, amount: Long, description: String, color: String){
        viewModelScope.launch {
            try {
                repository.addIncome(name, amount, color, description)
                _error.value = null
            } catch (e: Exception){
                _error.value = "Ошибка добавления ${e.message}"
            }
        }
    }


    // Обновить доход
    fun updateIncome(income: Income){
        viewModelScope.launch {
            try {
                repository.updateIncome(income)
                _error.value = null
            } catch (e: Exception){
                _error.value = "Ошибка обновления ${e.message}"
            }
        }
    }

    //Удалить расход
    fun deleteIncome(income: Income){
        viewModelScope.launch {
            try {
                repository.deleteIncome(income)
                _error.value = null
            } catch (e: Exception){
                _error.value = "Ошибка удаления ${e.message}"
            }
        }
    }

    // Очистить ошибку
    fun clearError() {
        _error.value = null
    }

    // Общая сумма доходов (ИСПРАВЛЕНО: теперь обновляется)
    private val _totalSum = MutableStateFlow<Long?>(null)
    val totalSum: StateFlow<Long?> = _totalSum.asStateFlow()

    //Текущий редактируемый пункт дохода
    private val _currentIncome = MutableStateFlow<Income?>(null)
    val currentIncome: StateFlow<Income?> = _currentIncome.asStateFlow()

    fun setCurrentIncome(income: Income){
        _currentIncome.value = income
    }

    // ====== ИНИЦИАЛИЗАЦИЯ ======

    init {
        loadIncome()
    }
}