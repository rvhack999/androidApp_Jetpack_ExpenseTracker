package com.example.expensetracker.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.TopBarScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    currentScreen: TopBarScreen,         // Текущий активный экран из вашего State / NavController
    onScreenSelected: (TopBarScreen) -> Unit // Колбэк смены экрана
) {
    TopAppBar(
        title = {
            // Используем SecondaryTabRow для красивой анимации линии
            SecondaryTabRow(
                selectedTabIndex = currentScreen.ordinal,
                modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                containerColor = Color.Transparent, // Прозрачный фон, чтобы подходил под ТопБар
                indicator = {
                    // Красивая закругленная линия-индикатор
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(currentScreen.ordinal)
                            .height(3.dp)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)),
                        color = MaterialTheme.colorScheme.primary // Цвет линии (например, фиолетовый)
                    )
                }
            ) {
                // Итерируемся по списку экранов для создания кнопок
                TopBarScreen.entries.forEach { screen ->
                    val isSelected = currentScreen == screen

                    // Плавная анимация цвета текста при переключении
                    val animatedTextColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        animationSpec = tween(durationMillis = 250),
                        label = "TextColorAnimation"
                    )

                    Tab(
                        selected = isSelected,
                        onClick = { onScreenSelected(screen) },
                        text = {
                            Text(
                                text = screen.title,
                                color = animatedTextColor,
                                fontSize = 15.sp,
                                // Делаем текст жирным, если экран активен
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }
    )
}