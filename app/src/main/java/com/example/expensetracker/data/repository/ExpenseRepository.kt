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
        start: String,
        end: String
    ): Flow<Long?> = expenseDao.getTotalForCategoryAndPeriod(
        categoryId = categoryId,
        startDate = start,
        endDate = end
    )

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
}