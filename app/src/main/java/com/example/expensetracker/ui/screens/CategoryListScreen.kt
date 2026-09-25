package com.example.expensetracker.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.viewmodel.CategoryViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.AppColors
import com.example.expensetracker.data.model.TopBarScreen
import com.example.expensetracker.ui.theme.CustomTopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    navController: NavController,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var deletingCategory by remember { mutableStateOf<Category?>(null) }
    val hasUncategorized by viewModel.hasUncategorizedExpenses.collectAsState()
    val categoriesWithUncategorized = if (hasUncategorized) {
        listOf(
            Category(
                id = -1,
                name = "Без категории",
                color = "#BDBDBD",
                isDefault = true
            )
        ) + categories
    } else {
        categories
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить категорию")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                categoriesWithUncategorized.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Нет категорий",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Нажмите + чтобы создать первую",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categoriesWithUncategorized, key = { it.id }) { category ->
                            CategoryCard(
                                category = category,
                                modifier = Modifier.animateItem(),
                                onClick = { navController.navigate("expenses/${category.id}") },
                                onEditClick = { editingCategory = category },
                                onDeleteClick = { deletingCategory = category }
                            )
                        }
                    }
                }
            }
        }
    }

    // Диалог добавления
    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, color ->
                viewModel.addCategory(name, color)
                showAddDialog = false
            }
        )
    }

    // Диалог редактирования
    if (editingCategory != null) {
        EditCategoryDialog(
            category = editingCategory!!,
            onDismiss = { editingCategory = null },
            onConfirm = { name, color ->
                val updated = editingCategory!!.copy(name = name, color = color)
                viewModel.updateCategory(updated)
                editingCategory = null
            }
        )
    }

    // Диалог удаления
    if (deletingCategory != null) {
        DeleteCategoryDialog(
            category = deletingCategory!!,
            onDismiss = { deletingCategory = null },
            onConfirm = {
                viewModel.deleteCategory(deletingCategory!!)
                deletingCategory = null
            }
        )
    }
}

@Composable
fun CategoryCard(
    category: Category,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Преобразуем HEX в Compose Color
    val backgroundColor = Color(category.color.toColorInt())

    // Тёмный текст для читаемости на пастельных фонах
    val contentColor = Color(0xFF1A1A1A)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Название (без кружка)
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

            // Кнопки (только для реальных категорий)
            if (category.id != -1) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        modifier = Modifier.size(20.dp),
                        tint = contentColor
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Удалить",
                        modifier = Modifier.size(20.dp),
                        tint = contentColor
                    )
                }
            }
        }
    }
}