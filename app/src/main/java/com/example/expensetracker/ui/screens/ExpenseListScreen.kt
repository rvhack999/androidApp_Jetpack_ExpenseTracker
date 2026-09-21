package com.example.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.HiltViewModelFactory
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.viewmodel.ExpenseViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    navController: NavController,
    categoryId: Int,
    backStackEntry: NavBackStackEntry,
    viewModel: ExpenseViewModel = viewModel(
        key = "expense_viewmodel_$categoryId",
        factory = HiltViewModelFactory(
            context = LocalContext.current,
            delegateFactory = backStackEntry.defaultViewModelProviderFactory
        )
    )
) {
    val expenses by viewModel.expenses.collectAsState()
    val plannedExpenses by viewModel.plannedExpenses.collectAsState()
    val showPlanned by viewModel.showPlanned.collectAsState()
    val totalSum by viewModel.totalSum.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val plannedTotalSum by viewModel.plannedTotalSum.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val displaySum = if (showPlanned) plannedTotalSum else totalSum
    var editingExpense by remember { mutableStateOf<Expense?>(null) }

    // Загружаем расходы при открытии экрана
    LaunchedEffect(categoryId) {
        viewModel.clearExpenses()
        viewModel.loadExpensesByCategory(categoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Расходы") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить расход")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showPlanned) "Планируемые" else "Выполненные",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = showPlanned,
                    onCheckedChange = { viewModel.toggleShowPlanned() }
                )
            }


            // Итоговая сумма
            if (displaySum != null && displaySum!! > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (showPlanned) {
                            MaterialTheme.colorScheme.surfaceVariant
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (showPlanned) "Запланировано:" else "Итого потрачено:",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "${displaySum!! / 100.0} ₽",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Список расходов
            val displayExpenses = if (showPlanned) plannedExpenses else expenses

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                displayExpenses.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (showPlanned) "Нет планируемых расходов" else "Нет расходов",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Нажмите + чтобы добавить",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(displayExpenses, key = { it.id }) { expense ->
                            ExpenseCard(
                                expense = expense,
                                modifier = Modifier.animateItem(),
                                onClick = { editingExpense = expense },
                                onDelete = { viewModel.deleteExpense(expense) }
                            )
                        }
                    }
                }
            }


        }
        if (showAddDialog) {
            AddExpenseDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { amount, description, isPlanned ->
                    viewModel.addExpense(
                        amount = amount,
                        categoryId = categoryId,
                        description = description,
                        isPlanned = isPlanned
                    )
                    showAddDialog = false
                }
            )

        }
        if (editingExpense != null) {
            EditExpenseDialog(
                expense = editingExpense!!,
                onDismiss = { editingExpense = null },
                onConfirm = { amount, description, isPlanned ->
                    val updated = editingExpense!!.copy(
                        amount = amount,
                        description = description,
                        isPlanned = isPlanned
                    )
                    viewModel.updateExpense(updated)
                    editingExpense = null
                }
            )
        }
    }
}

@Composable
fun ExpenseCard(
    expense: Expense,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable{onClick()},
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (expense.isPlanned) {
                MaterialTheme.colorScheme.surfaceVariant  // серый для планируемых
            } else {
                MaterialTheme.colorScheme.surface
            }
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
                    text = "${expense.amount / 100.0} ₽",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (expense.isPlanned) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                if (expense.description.isNotBlank()) {
                    Text(
                        text = expense.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = expense.date.take(10),
                    style = MaterialTheme.typography.labelSmall
                )

                if (expense.isPlanned) {
                    Text(
                        text = "⏳ Планируемый",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}