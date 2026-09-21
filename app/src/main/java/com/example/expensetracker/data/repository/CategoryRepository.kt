package com.example.expensetracker.data.repository

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.data.db.CategoryDao
import com.example.expensetracker.data.model.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository (private val categoryDao: CategoryDao){

    // Все категории
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    // Одна категория
    suspend fun getCategoryById(categoryId: Int): Category? = categoryDao.getCategoryById(categoryId = categoryId)

    // Создать категорию
    suspend fun addCategory(
        name: String,
        color: Color,
    ): Long {
        val category = Category(
            name = name,
            color = color,
            isDefault = false
        )
        return categoryDao.insertCategory(category)
    }

    // Обновить
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    // Удалить
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
}