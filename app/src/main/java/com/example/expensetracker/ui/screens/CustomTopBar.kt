package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.TopBarScreen

@Composable
fun CustomTopBar(
    screenType: TopBarScreen,
    onScreenSelected: (TopBarScreen) -> Unit
) {
    Row {
        TopBarScreen.values().forEach { screen ->
            TextButton(
                onClick = { onScreenSelected(screen) },
                enabled = screen != screenType // Отключаем клик на уже активную вкладку
            ) {
                Text(
                    text = screen.title,
                    // Здесь можно добавить стили (например, сделать текст жирным, если screen == screenType)
                )
            }
        }
    }
}
