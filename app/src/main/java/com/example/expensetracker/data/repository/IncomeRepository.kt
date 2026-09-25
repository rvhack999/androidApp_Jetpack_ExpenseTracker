package com.example.expensetracker.data.repository

import com.example.expensetracker.data.database.IncomeDao
import com.example.expensetracker.data.model.Income
import com.example.expensetracker.data.utils.getCurrentDateTime
import kotlinx.coroutines.flow.Flow


class IncomeRepository(private val incomeDao: IncomeDao) {

    // Все доходы
    fun getAllIncome(): Flow<List<Income>> = incomeDao.getAllIncome()

    // Общая сумма за период
    fun getTotalIncomeForPeriod(startDate: String, endDate: String) = incomeDao.getTotalIncomeForPeriod(startDate = startDate, endDate = endDate)

    // Один пункт дохода
    suspend fun getIncomeById(incomeId: Int): Income? = incomeDao.getIncomeById(incomeId = incomeId)

    // Добавить пункт дохода
    suspend fun addIncome(
        name: String,
        amount: Long,
        color: String,
        description: String
    ){
        val income = Income(
            name = name,
            amount = amount,
            date = getCurrentDateTime(),
            description = description,
            color = color
        )
    }

    // Обновить
    suspend fun updateIncome(income: Income) = incomeDao.updateIncome(income)

    // Удалить
    suspend fun deleteIncome(income: Income) = incomeDao.deleteIncome(income)
}