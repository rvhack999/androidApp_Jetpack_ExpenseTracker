package com.example.expensetracker.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {

    // ====== СОСТОЯНИЯ ======

    // Список категорий
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // Состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Выбранная категория (для редактирования)
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    // ====== ИНИЦИАЛИЗАЦИЯ ======

    init {
        loadCategories()
    }

    // ====== ЗАГРУЗКА ДАННЫХ ======

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getAllCategories().collect { categories ->
                    _categories.value = categories
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // ====== ДЕЙСТВИЯ ======

    // Создать категорию
    fun addCategory(name: String, color: Color) {
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