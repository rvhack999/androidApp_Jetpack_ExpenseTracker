package com.example.expensetracker.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.model.Category
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoryDao{

    // все категории, сортировка по имени (A→Z)
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    // одна категория (для редактирования)
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Int): Category?

    // добавить
    @Insert
    suspend fun insertCategory(category: Category): Long

    // обновить
    @Update
    suspend fun updateCategory(category: Category)

    // удалить
    @Delete
    suspend fun deleteCategory(category: Category)
}