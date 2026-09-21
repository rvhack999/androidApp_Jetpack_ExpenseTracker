package com.example.expensetracker.di

import android.content.Context
import com.example.expensetracker.data.database.AppDatabase
import com.example.expensetracker.data.database.CategoryDao
import com.example.expensetracker.data.database.ExpenseDao
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao =
        database.ExpenseDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao =
        database.CategoryDao()

    @Provides
    @Singleton
    fun provideExpenseRepository(expenseDao: ExpenseDao): ExpenseRepository {
        return ExpenseRepository(expenseDao)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository {
        return CategoryRepository(categoryDao)
    }
}