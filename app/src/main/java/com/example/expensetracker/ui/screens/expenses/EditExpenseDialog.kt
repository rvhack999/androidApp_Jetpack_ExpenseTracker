package com.example.expensetracker.ui.screens.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Expense

@Composable
fun EditExpenseDialog(
    expense: Expense,
    onDismiss: () -> Unit,
    onConfirm: (amount: Long, description: String, isPlanned: Boolean) -> Unit
) {
    // Предзаполняем поля из expense
    var amountText by remember {
        mutableStateOf((expense.amount / 100.0).toString())
    }
    var description by remember { mutableStateOf(expense.description) }
    var isPlanned by remember { mutableStateOf(expense.isPlanned) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать расход") },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amountText = input
                        }
                    },
                    label = { Text("Сумма (₽)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPlanned,
                        onCheckedChange = { isPlanned = it }
                    )
                    Text("Планируемый расход")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountText.toDoubleOrNull()?.times(100)?.toLong() ?: 0L
                    if (amount > 0) {
                        onConfirm(amount, description, isPlanned)
                    }
                },
                enabled = amountText.isNotBlank()
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}