package com.example.expensetracker.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.expensetracker.data.database.AppDatabase
import com.example.expensetracker.data.database.CategoryDao
import com.example.expensetracker.data.database.ExpenseDao
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExpenseDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var categoryDao: CategoryDao

    private var foodCategoryId: Int = 0
    private var transportCategoryId: Int = 0

    @Before
    fun setup() = runTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        expenseDao = database.ExpenseDao()
        categoryDao = database.CategoryDao()

        // Создаём две категории для тестов
        foodCategoryId = categoryDao.insertCategory(
            Category(name = "Еда", color = "#FFD1DC")
        ).toInt()

        transportCategoryId = categoryDao.insertCategory(
            Category(name = "Транспорт", color = "#7FB5B5")
        ).toInt()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ====== БАЗОВЫЕ CRUD ======

    @Test
    fun insertExpense_andGetByCategory_returnsExpense() = runTest {
        // Arrange
        val expense = Expense(
            amount = 15050L,  // 150.50 ₽
            categoryId = foodCategoryId,
            description = "Обед",
            date = "2024-01-15T12:00:00",
            color = "#FFD1DC",
            isPlanned = false
        )

        // Act
        expenseDao.insertExpense(expense)
        val expenses = expenseDao.getExpensesByCategory(foodCategoryId).first()

        // Assert
        assertEquals(1, expenses.size)
        assertEquals(15050L, expenses[0].amount)
        assertEquals("Обед", expenses[0].description)
        assertEquals(foodCategoryId, expenses[0].categoryId)
    }

    @Test
    fun getExpensesByCategory_returnsOnlyThisCategory() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(foodCategoryId, 10000L, "Обед"))
        expenseDao.insertExpense(createExpense(foodCategoryId, 5000L, "Кофе"))
        expenseDao.insertExpense(createExpense(transportCategoryId, 3000L, "Метро"))

        // Act
        val foodExpenses = expenseDao.getExpensesByCategory(foodCategoryId).first()
        val transportExpenses = expenseDao.getExpensesByCategory(transportCategoryId).first()

        // Assert
        assertEquals(2, foodExpenses.size)
        assertEquals(1, transportExpenses.size)
        assertTrue(foodExpenses.all { it.categoryId == foodCategoryId })
        assertTrue(transportExpenses.all { it.categoryId == transportCategoryId })
    }

    @Test
    fun getExpensesByCategory_doesNotReturnPlanned() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(foodCategoryId, 10000L, "Обед", isPlanned = false))
        expenseDao.insertExpense(createExpense(foodCategoryId, 5000L, "Ужин", isPlanned = true))

        // Act
        val expenses = expenseDao.getExpensesByCategory(foodCategoryId).first()

        // Assert — только выполненные
        assertEquals(1, expenses.size)
        assertEquals("Обед", expenses[0].description)
    }

    @Test
    fun getPlannedExpensesByCategory_returnsOnlyPlanned() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(foodCategoryId, 10000L, "Обед", isPlanned = false))
        expenseDao.insertExpense(createExpense(foodCategoryId, 5000L, "Ужин", isPlanned = true))
        expenseDao.insertExpense(createExpense(foodCategoryId, 3000L, "Завтрак", isPlanned = true))

        // Act
        val planned = expenseDao.getPlannedExpensesByCategory(foodCategoryId).first()

        // Assert
        assertEquals(2, planned.size)
        assertTrue(planned.all { it.isPlanned })
    }

    // ====== БЕЗ КАТЕГОРИИ ======

    @Test
    fun getUncategorizedExpenses_returnsExpensesWithNullCategory() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(foodCategoryId, 10000L, "Обед"))
        expenseDao.insertExpense(createExpense(null, 5000L, "Без категории"))

        // Act
        val uncategorized = expenseDao.getUncategorizedExpenses().first()

        // Assert
        assertEquals(1, uncategorized.size)
        assertNull(uncategorized[0].categoryId)
        assertEquals("Без категории", uncategorized[0].description)
    }

    @Test
    fun getUncategorizedCount_returnsCorrectNumber() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(foodCategoryId, 10000L, "Обед"))
        expenseDao.insertExpense(createExpense(null, 5000L, "Без категории 1"))
        expenseDao.insertExpense(createExpense(null, 3000L, "Без категории 2"))

        // Act
        val count = expenseDao.getUncategorizedCount().first()

        // Assert
        assertEquals(2, count)
    }

    @Test
    fun deleteCategory_setsExpenseCategoryIdToNull() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(foodCategoryId, 10000L, "Обед"))
        val category = categoryDao.getCategoryById(foodCategoryId)!!

        // Act — удаляем категорию
        categoryDao.deleteCategory(category)

        // Assert — расход остался, но categoryId = null
        val allExpenses = expenseDao.getAllExpenses().first()
        assertEquals(1, allExpenses.size)
        assertNull(allExpenses[0].categoryId)
    }

    // ====== СУММЫ ======

    @Test
    fun getTotalForCategoryAndPeriod_returnsCorrectSum() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(
            foodCategoryId, 10000L, "Обед", date = "2024-01-15T12:00:00"
        ))
        expenseDao.insertExpense(createExpense(
            foodCategoryId, 5000L, "Кофе", date = "2024-01-20T15:00:00"
        ))
        // Вне периода
        expenseDao.insertExpense(createExpense(
            foodCategoryId, 99999L, "Старое", date = "2023-12-01T12:00:00"
        ))

        // Act
        val sum = expenseDao.getTotalForCategoryAndPeriod(
            foodCategoryId,
            "2024-01-01T00:00:00",
            "2024-01-31T23:59:59"
        ).first()

        // Assert
        assertEquals(15000L, sum)  // 10000 + 5000
    }

    @Test
    fun getTotalForCategoryAndPeriod_noExpenses_returnsNull() = runTest {
        // Act
        val sum = expenseDao.getTotalForCategoryAndPeriod(
            foodCategoryId,
            "2024-01-01T00:00:00",
            "2024-01-31T23:59:59"
        ).first()

        // Assert
        assertNull(sum)
    }

    @Test
    fun getTotalForCategoryAndPeriod_doesNotCountPlanned() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(
            foodCategoryId, 10000L, "Обед", date = "2024-01-15T12:00:00", isPlanned = false
        ))
        expenseDao.insertExpense(createExpense(
            foodCategoryId, 5000L, "Планируемый", date = "2024-01-20T15:00:00", isPlanned = true
        ))

        // Act
        val sum = expenseDao.getTotalForCategoryAndPeriod(
            foodCategoryId,
            "2024-01-01T00:00:00",
            "2024-01-31T23:59:59"
        ).first()

        // Assert — только выполненные
        assertEquals(10000L, sum)
    }

    @Test
    fun getPlannedTotalForCategoryAndPeriod_countsOnlyPlanned() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(
            foodCategoryId, 10000L, "Обед", date = "2024-01-15T12:00:00", isPlanned = false
        ))
        expenseDao.insertExpense(createExpense(
            foodCategoryId, 5000L, "Планируемый", date = "2024-01-20T15:00:00", isPlanned = true
        ))

        // Act
        val sum = expenseDao.getPlannedTotalForCategoryAndPeriod(
            foodCategoryId,
            "2024-01-01T00:00:00",
            "2024-01-31T23:59:59"
        ).first()

        // Assert — только планируемые
        assertEquals(5000L, sum)
    }

    // ====== ОБНОВЛЕНИЕ И УДАЛЕНИЕ ======

    @Test
    fun updateExpense_changesAmountAndDescription() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(foodCategoryId, 10000L, "Обед"))
        val original = expenseDao.getExpensesByCategory(foodCategoryId).first()[0]

        // Act
        val updated = original.copy(amount = 20000L, description = "Ужин")
        expenseDao.updateExpense(updated)
        val result = expenseDao.getExpensesByCategory(foodCategoryId).first()[0]

        // Assert
        assertEquals(20000L, result.amount)
        assertEquals("Ужин", result.description)
    }

    @Test
    fun deleteExpense_removesFromDatabase() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(foodCategoryId, 10000L, "Обед"))
        val expense = expenseDao.getExpensesByCategory(foodCategoryId).first()[0]

        // Act
        expenseDao.deleteExpense(expense)

        // Assert
        assertTrue(expenseDao.getExpensesByCategory(foodCategoryId).first().isEmpty())
    }

    @Test
    fun getExpenseCountInCategory_returnsCorrectNumber() = runTest {
        // Arrange
        expenseDao.insertExpense(createExpense(foodCategoryId, 10000L, "Обед"))
        expenseDao.insertExpense(createExpense(foodCategoryId, 5000L, "Кофе"))
        expenseDao.insertExpense(createExpense(transportCategoryId, 3000L, "Метро"))

        // Act
        val count = expenseDao.getExpenseCountInCategory(foodCategoryId)

        // Assert
        assertEquals(2, count)
    }

    // ====== ВСПОМОГАТЕЛЬНАЯ ФУНКЦИЯ ======

    private fun createExpense(
        categoryId: Int?,
        amount: Long,
        description: String,
        date: String = "2024-01-15T12:00:00",
        isPlanned: Boolean = false
    ): Expense {
        return Expense(
            amount = amount,
            categoryId = categoryId,
            description = description,
            date = date,
            color = "#FFFFFF",
            isPlanned = isPlanned
        )
    }
}