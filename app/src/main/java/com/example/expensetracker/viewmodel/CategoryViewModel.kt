package com.example.expensetracker.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    // ====== СОСТОЯНИЯ ======

    // Список категорий
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // Состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Есть ли расходы без категории
    private val _hasUncategorizedExpenses = MutableStateFlow(false)
    val hasUncategorizedExpenses: StateFlow<Boolean> = _hasUncategorizedExpenses.asStateFlow()

    // Ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Выбранная категория (для редактирования)
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    // ====== ИНИЦИАЛИЗАЦИЯ ======

    init {
        loadCategories()
        observeUncategorizedExpenses()
    }

    private fun observeUncategorizedExpenses() {
        viewModelScope.launch {
            expenseRepository.getUncategorizedCount().collect { count ->
                _hasUncategorizedExpenses.value = count > 0
            }
        }
    }

    // ====== ЗАГРУЗКА ДАННЫХ ======

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getAllCategories().collect { categories ->
                    _categories.value = categories
                    _isLoading.value = false  // ← СБРАСЫВАЕМ ЗДЕСЬ
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // ====== ДЕЙСТВИЯ ======

    // Создать категорию
    fun addCategory(name: String, color: String) {
        viewModelScope.launch {
            try {
                repository.addCategory(name, color)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка создания: ${e.message}"
            }
        }
    }

    // Обновить категорию
    fun updateCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.updateCategory(category)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка обновления: ${e.message}"
            }
        }
    }

    // Удалить категорию
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(category)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка удаления: ${e.message}"
            }
        }
    }

    // Выбрать категорию для редактирования
    fun selectCategory(category: Category?) {
        _selectedCategory.value = category
    }

    // Очистить ошибку
    fun clearError() {
        _error.value = null
    }
}