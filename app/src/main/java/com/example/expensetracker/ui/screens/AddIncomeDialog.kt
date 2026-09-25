package com.example.expensetracker.ui.screens

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

@Composable
fun AddIncomeDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        amount: Long,
        description: String,
        color: String
    ) -> Unit
){
    var nameText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый доход") },
        text = {
            Column {
                // Сумма
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amountText = input
                        }
                    },
                    label = { Text("Сумма (₽)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0.00") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Описание
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание (необязательно)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountText.toDoubleOrNull()?.times(100)?.toLong() ?: 0L
                    if (amount > 0) {
                        onConfirm(nameText,amount, description, color)
                    }
                },
                enabled = amountText.isNotBlank() && amountText.toDoubleOrNull() != null
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )

}