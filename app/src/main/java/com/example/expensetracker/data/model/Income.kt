package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "income")
data class Income(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val amount: Long,
    val date: String,
    val description: String,
    val color: String,
)
