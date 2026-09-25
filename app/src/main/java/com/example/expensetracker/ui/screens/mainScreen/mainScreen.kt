package com.example.expensetracker.ui.screens.mainScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.data.model.AppColors
import com.example.expensetracker.viewmodel.ExpenseViewModel
import com.example.expensetracker.viewmodel.IncomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModelIncome: IncomeViewModel = hiltViewModel(),
    viewModelExpense: ExpenseViewModel = hiltViewModel()
) {
    val totalSumIncome by viewModelIncome.totalSum.collectAsState()
    val totalSumExpenseWithoutCategoryAndPlanned by viewModelExpense.totalSumWithoutCategoryAndPlanned.collectAsState()
    val totalSumPlannedExpensesWithoutCategory by viewModelExpense.totalSumPlannedExpensesWithoutCategory.collectAsState()
    val incomeValue = totalSumIncome ?: 0L
    val expenseValue = totalSumExpenseWithoutCategoryAndPlanned ?: 0L
    val plannedExpense = totalSumPlannedExpensesWithoutCategory ?: 0L
    val remainingBalance = incomeValue - expenseValue
    val reminingPlanned = remainingBalance - plannedExpense

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    // Блок 1: Доходы
                    CardUnit(
                        text = "Остаток:",
                        value = remainingBalance,
                        color = if (remainingBalance > 0.0){
                            AppColors.Fern} else {
                            AppColors.SoftYellowPink}
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Блок 2: Расходы
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column() {
                            CardUnit(
                                text = "Доходы:",
                                value = totalSumIncome,
                                color = Color.Black
                            )
                        }
                        Column() {
                            CardUnit(
                                text = "Расходы:",
                                value = totalSumExpenseWithoutCategoryAndPlanned,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Блок 3: Планируемые расходы
                    if(totalSumPlannedExpensesWithoutCategory?.toDouble() != 0.0){
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column() {
                                CardUnit(
                                    text = "Запланировано:",
                                    value = totalSumPlannedExpensesWithoutCategory,
                                    color = Color.Gray
                                )
                            }

                            Column() {
                                CardUnit(
                                    text = if (reminingPlanned > 0){
                                        "Хватит и еще остается:"
                                    }
                                    else "Не хватит:",
                                    value = reminingPlanned,
                                    color = if (reminingPlanned > 0){
                                        AppColors.SoftTurquoise
                                    } else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardUnit(
    text: String,
    value: Long?,
    color: Color
)
{
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium
    )
    val current = value ?: 0L
    Text(
        text = "${current / 100.0} ₽",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = color
    )
}
