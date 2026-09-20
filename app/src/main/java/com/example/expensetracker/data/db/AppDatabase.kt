package com.example.expensetracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Expense

@Database(
    entities = [Expense::class, Category::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase(){
    abstract fun ExpenseDao(): ExpenseDao
    abstract fun CategoryDao(): CategoryDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}