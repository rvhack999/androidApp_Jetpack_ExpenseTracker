package com.example.expensetracker.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.expensetracker.data.model.Category
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        categoryDao = database.CategoryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ====== СОЗДАНИЕ ======

    @Test
    fun insertCategory_andGetAll_returnsCategory() = runTest {
        // Arrange
        val category = Category(name = "Еда", color = "#FFD1DC")

        // Act
        categoryDao.insertCategory(category)
        val categories = categoryDao.getAllCategories().first()

        // Assert
        assertEquals(1, categories.size)
        assertEquals("Еда", categories[0].name)
        assertEquals("#FFD1DC", categories[0].color)
        assertTrue(categories[0].id > 0)  // ID сгенерирован
    }

    @Test
    fun insertCategory_withDefaultFalse_isNotDefault() = runTest {
        // Arrange
        val category = Category(name = "Моя категория", color = "#FFD1DC")

        // Act
        categoryDao.insertCategory(category)
        val result = categoryDao.getAllCategories().first()[0]

        // Assert
        assertTrue(!result.isDefault)
    }

    @Test
    fun insertMultipleCategories_andGetAll_returnsSortedByName() = runTest {
        // Arrange
        categoryDao.insertCategory(Category(name = "Транспорт", color = "#7FB5B5"))
        categoryDao.insertCategory(Category(name = "Еда", color = "#FFD1DC"))
        categoryDao.insertCategory(Category(name = "Развлечения", color = "#E6E6FA"))

        // Act
        val categories = categoryDao.getAllCategories().first()

        // Assert — сортировка по имени A→Z
        assertEquals(3, categories.size)
        assertEquals("Еда", categories[0].name)
        assertEquals("Развлечения", categories[1].name)
        assertEquals("Транспорт", categories[2].name)
    }

    // ====== ЧТЕНИЕ ======

    @Test
    fun getCategoryById_returnsCorrectCategory() = runTest {
        // Arrange
        val id1 = categoryDao.insertCategory(Category(name = "Еда", color = "#FFD1DC"))
        categoryDao.insertCategory(Category(name = "Транспорт", color = "#7FB5B5"))

        // Act
        val category = categoryDao.getCategoryById(id1.toInt())

        // Assert
        assertEquals("Еда", category?.name)
        assertEquals("#FFD1DC", category?.color)
    }

    @Test
    fun getCategoryById_nonExistent_returnsNull() = runTest {
        // Act
        val category = categoryDao.getCategoryById(999)

        // Assert
        assertNull(category)
    }

    // ====== ОБНОВЛЕНИЕ ======

    @Test
    fun updateCategory_changesNameAndColor() = runTest {
        // Arrange
        val id = categoryDao.insertCategory(Category(name = "Еда", color = "#FFD1DC"))
        val original = categoryDao.getCategoryById(id.toInt())!!

        // Act
        val updated = original.copy(name = "Вкусная еда", color = "#FF0000")
        categoryDao.updateCategory(updated)
        val result = categoryDao.getCategoryById(id.toInt())

        // Assert
        assertEquals("Вкусная еда", result?.name)
        assertEquals("#FF0000", result?.color)
    }

    @Test
    fun updateCategory_keepsSameId() = runTest {
        // Arrange
        val id = categoryDao.insertCategory(Category(name = "Еда", color = "#FFD1DC"))
        val original = categoryDao.getCategoryById(id.toInt())!!

        // Act
        val updated = original.copy(name = "Новое имя")
        categoryDao.updateCategory(updated)
        val result = categoryDao.getCategoryById(id.toInt())

        // Assert — ID не изменился
        assertEquals(id.toInt(), result?.id)
    }

    // ====== УДАЛЕНИЕ ======

    @Test
    fun deleteCategory_removesFromDatabase() = runTest {
        // Arrange
        val id = categoryDao.insertCategory(Category(name = "Еда", color = "#FFD1DC"))
        val category = categoryDao.getCategoryById(id.toInt())!!

        // Act
        categoryDao.deleteCategory(category)

        // Assert
        assertNull(categoryDao.getCategoryById(id.toInt()))
        assertTrue(categoryDao.getAllCategories().first().isEmpty())
    }

    @Test
    fun deleteCategory_doesNotAffectOtherCategories() = runTest {
        // Arrange
        val id1 = categoryDao.insertCategory(Category(name = "Еда", color = "#FFD1DC"))
        val id2 = categoryDao.insertCategory(Category(name = "Транспорт", color = "#7FB5B5"))

        val category1 = categoryDao.getCategoryById(id1.toInt())!!

        // Act
        categoryDao.deleteCategory(category1)

        // Assert — вторая категория осталась
        val remaining = categoryDao.getAllCategories().first()
        assertEquals(1, remaining.size)
        assertEquals("Транспорт", remaining[0].name)
    }

    // ====== EDGE CASES ======

    @Test
    fun insertCategory_withSameName_createsTwoCategories() = runTest {
        // Arrange — Room не запрещает дубликаты по имени
        categoryDao.insertCategory(Category(name = "Еда", color = "#FFD1DC"))
        categoryDao.insertCategory(Category(name = "Еда", color = "#FF0000"))

        // Act
        val categories = categoryDao.getAllCategories().first()

        // Assert — обе сохранены
        assertEquals(2, categories.size)
    }

    @Test
    fun getAllCategories_emptyDatabase_returnsEmptyList() = runTest {
        // Act
        val categories = categoryDao.getAllCategories().first()

        // Assert
        assertTrue(categories.isEmpty())
    }

    @Test
    fun insertCategory_withEmptyName_saves() = runTest {
        // Arrange
        val category = Category(name = "", color = "#FFD1DC")

        // Act
        categoryDao.insertCategory(category)
        val result = categoryDao.getAllCategories().first()

        // Assert — Room не валидирует данные, пустое имя сохраняется
        assertEquals(1, result.size)
        assertEquals("", result[0].name)
    }
}