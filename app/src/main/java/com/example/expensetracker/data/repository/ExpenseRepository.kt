package com.example.expensetracker.data.repository

import androidx.compose.ui.graphics.Color
import com.example.expensetracker.data.database.ExpenseDao
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.utils.getCurrentDateTime
import kotlinx.coroutines.flow.Flow


class ExpenseRepository(
    private val expenseDao: ExpenseDao
){
    // Все расходы	Просто вызывает dao.getAllExpenses()
    fun getAllExpenses(): Flow<List<Expense>> =expenseDao.getAllExpenses()

    // По категории
    fun getExpensesByCategory(categoryId: Int): Flow<List<Expense>> = expenseDao.getExpensesByCategory(categoryId)

    // Планируемые
    fun getPlannedExpenses(): Flow<List<Expense>> = expenseDao.getPlannedExpenses()

    // Сумма за период
    fun getTotalForPeriod(
        startDate: String,
        endDate: String
    ): Flow<Long?> = expenseDao.getTotalForPeriod(startDate = startDate, endDate = endDate)

    // Сумма по категории
    fun getTotalForCategoryAndPeriod(
        categoryId: Int,
        startDate: String,
        endDate: String
    ): Flow<Long?> =
        expenseDao.getTotalForCategoryAndPeriod(categoryId, startDate, endDate)

    fun getPlannedTotalForCategoryAndPeriod(
        categoryId: Int,
        startDate: String,
        endDate: String
    ): Flow<Long?> =
        expenseDao.getPlannedTotalForCategoryAndPeriod(categoryId, startDate, endDate)

    // Добавить расход
    suspend fun addExpense(
        amount: Long,
        categoryId: Int?,
        description: String,
        isPlanned: Boolean
    ){
        val expense = Expense(
            amount = amount,
            categoryId = categoryId,
            description = description,
            date = getCurrentDateTime(),
            color = "0xFFF5F5DC",
            isPlanned = isPlanned
        )
        expenseDao.insertExpense(expense)
    }

    // Обновить
    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    // Удалить
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    // Количество в категории
    suspend fun getExpenseCountInCategory(categoryId: Int): Int = expenseDao.getExpenseCountInCategory(categoryId = categoryId)

    fun getPlannedExpensesByCategory(categoryId: Int): Flow<List<Expense>> =
        expenseDao.getPlannedExpensesByCategory(categoryId)

    fun getUncategorizedPlannedExpenses(): Flow<List<Expense>> =
        expenseDao.getUncategorizedPlannedExpenses()

    fun getUncategorizedTotalForPeriod(startDate: String, endDate: String): Flow<Long?> =
        expenseDao.getUncategorizedTotalForPeriod(startDate, endDate)

    fun getUncategorizedPlannedTotalForPeriod(startDate: String, endDate: String): Flow<Long?> =
        expenseDao.getUncategorizedPlannedTotalForPeriod(startDate, endDate)

    fun getUncategorizedCount(): Flow<Int> =
        expenseDao.getUncategorizedCount()

    fun getUncategorizedExpenses(): Flow<List<Expense>> =
        expenseDao.getUncategorizedExpenses()
}