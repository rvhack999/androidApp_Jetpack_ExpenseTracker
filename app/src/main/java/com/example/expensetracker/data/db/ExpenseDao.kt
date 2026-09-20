package com.example.expensetracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    // 1. Все расходы, сначала новые
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    // 2. По категории, сначала новые
    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByCategory(categoryId: Int): Flow<List<Expense>>

    // 3. Планируемые расходы
    @Query("SELECT * FROM expenses WHERE isPlanned = 1 ORDER BY date DESC")
    fun getPlannedExpenses(): Flow<List<Expense>>

    // 4. Общая сумма за период
    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalForPeriod(startDate: String, endDate: String): Flow<Long?>

    // 5. Сумма по категории за период
    @Query("SELECT SUM(amount) FROM expenses WHERE categoryId = :categoryId AND date BETWEEN :startDate AND :endDate")
    fun getTotalForCategoryAndPeriod(categoryId: Int, startDate: String, endDate: String): Flow<Long?>

    // 6. Количество расходов в категории
    @Query("SELECT COUNT(*) FROM expenses WHERE categoryId = :categoryId")
    suspend fun getExpenseCountInCategory(categoryId: Int): Int

    // 7. Добавить расход
    @Insert
    suspend fun insertExpense(expense: Expense): Long

    // 8. Обновить расход
    @Update
    suspend fun updateExpense(expense: Expense)

    // 9. Удалить расход
    @Delete
    suspend fun deleteExpense(expense: Expense)
}