package com.example.expensetracker.ui.screens.incoms


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Income

@Composable
fun EditIncomeDialog(
    income: Income,
    onDismiss: () -> Unit,
    onConfirm: (amount: Long, description: String) -> Unit
) {
    // Безопасно переводим копейки в рубли для отображения пользователю
    var amountText by remember {
        mutableStateOf((income.amount / 100.0).toString())
    }
    var description by remember { mutableStateOf(income.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать доход") },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        // Регулярное выражение разрешает только ввод чисел с одной точкой
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Переводим рубли обратно в Long (копейки) для базы данных
                    val amount = amountText.toDoubleOrNull()?.times(100)?.toLong() ?: 0L
                    if (amount > 0) {
                        onConfirm(amount, description)
                    }
                },
                enabled = amountText.isNotBlank() && amountText.toDoubleOrNull() != null
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