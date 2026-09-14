package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "expenses", foreignKeys = [ForeignKey(TODO())])
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val amount: Long,
    val categoryId: Int? = null,
    val description: String = "",
    val date: String,
    val color: String,
    val isPlanned: Boolean
)
