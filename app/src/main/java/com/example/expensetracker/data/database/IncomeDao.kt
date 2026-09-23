package com.example.expensetracker.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.model.Income
import kotlinx.coroutines.flow.Flow


@Dao
interface IncomeDao {

    // 1. Все доходы, сначала новые
    @Query("SELECT * FROM income ORDER BY date DESC")
    fun getAllIncome(): Flow<List<Income>>

    // 2. Общая сумма за период
    @Query("SELECT SUM(amount) FROM income WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalIncomeForPeriod(startDate: String, endDate: String): Flow<Long?>

    // 3. Выбор одного пункта дохода для редактирования
    @Query("SELECT * FROM income WHERE id = :incomeId")
    suspend fun getIncomeById(incomeId: Int): Income

    @Insert
    suspend fun insertIncome(income: Income)

    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)
}