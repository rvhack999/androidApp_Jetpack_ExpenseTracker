package com.example.expensetracker.ui.screens.incoms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.data.model.Income
import com.example.expensetracker.viewmodel.IncomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeListScreen(
    navController: NavController,
    viewModel: IncomeViewModel = hiltViewModel()
){
    val incomes by viewModel.income.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val totalSum by viewModel.totalSum.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingIncome by remember { mutableStateOf<Income?>(null) }

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
            // Итоговая сумма (ИСПРАВЛЕНО: отображается ВСЕГДА, без условий if)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Доходы:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    // Безопасное приведение к Long: если null, то 0L
                    val currentTotal = totalSum ?: 0L
                    Text(
                        text = "${currentTotal / 100.0} ₽",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Список расходов / доходов (Сдвигаем вниз, чтобы не перекрывал карточку суммы)
            val displayIncome = incomes

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                displayIncome.isEmpty() -> {
                    Text(
                        text = "Список пуст",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 100.dp), // Отступ сверху, чтобы список не залезал ПОД карточку суммы!
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displayIncome, key = { it.id }) { income ->
                            IncomeCard(
                                income = income,
                                modifier = Modifier.animateItem(),
                                onClick = { editingIncome = income },
                                onDelete = { viewModel.deleteIncome(income) }
                            )
                        }
                    }
                }
            }
        }

        // Диалог добавления
        if (showAddDialog) {
            AddIncomeDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, amount, description, color ->
                    viewModel.addIncome(
                        name = name,
                        amount = amount,
                        description = description,
                        color = color
                    )
                    showAddDialog = false
                }
            )
        }

        // Диалог редактирования
        if (editingIncome != null) {
            EditIncomeDialog(
                income = editingIncome!!,
                onDismiss = { editingIncome = null },
                onConfirm = { amount, description ->
                    // Берем старый объект и безопасно обновляем только измененные поля
                    val updated = editingIncome!!.copy(
                        amount = amount,
                        description = description,
                        // Если оригинальные поля были пустыми, подставляем заглушки, чтобы Room не падал
                        name = editingIncome!!.name.ifBlank { "Доход" },
                        color = editingIncome!!.color.ifBlank { "#BDBDBD" },
                        date = editingIncome!!.date.ifBlank { "2026-01-01T00:00:00" }
                    )
                    viewModel.updateIncome(updated)
                    editingIncome = null
                }
            )
        }

    }
}

@Composable
fun IncomeCard(
    income: Income,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${income.amount / 100.0} ₽",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!income.description.isNullOrBlank()) {
                    Text(
                        text = income.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp) // Небольшой отступ между суммой и описанием
                    )
                }

                // ИСПРАВЛЕНО: Безопасное отображение даты без падений
                val displayDate = if (income.date.length >= 10) {
                    income.date.take(10)
                } else {
                    income.date.ifBlank { "Нет даты" }
                }

                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}
