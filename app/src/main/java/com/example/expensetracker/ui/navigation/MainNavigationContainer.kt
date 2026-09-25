package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.data.model.TopBarScreen
import com.example.expensetracker.ui.screens.CategoryListScreen
import com.example.expensetracker.ui.screens.ExpenseListScreen
import com.example.expensetracker.ui.screens.IncomeListScreen
import com.example.expensetracker.ui.theme.CustomTopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationContainer() {
    val navController = rememberNavController()

    // Подписываемся на состояние навигационного стека
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Динамически определяем состояние TopBarScreen на основе текущего маршрута
    val currentScreen = when {
        currentRoute == "income" -> TopBarScreen.INCOME
        currentRoute == "categories" || currentRoute?.startsWith("expenses/") == true -> TopBarScreen.EXPENSES
        currentRoute == "graphics" -> TopBarScreen.GRAPHICS
        else -> TopBarScreen.EXPENSES
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    // Передаем текущий активный экран в ваш кастомный бар
                    CustomTopBar(currentScreen) { selectedScreen ->
                        // Логика переключения экранов при клике на элементы CustomTopBar
                        when (selectedScreen) {
                            TopBarScreen.INCOME -> {
                                navController.navigate("income") {
                                    popUpTo("categories") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            TopBarScreen.EXPENSES -> {
                                navController.navigate("categories") {
                                    popUpTo("categories") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            TopBarScreen.GRAPHICS -> {
                                // Навигация на экран графиков, когда он будет готов
                                // navController.navigate("graphics")
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "categories", // Начальный экран (список категорий расходов)
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp)
        ) {
            // ЭКРАН 1: Список категорий расходов
            composable("categories") {
                CategoryListScreen(navController = navController)
            }

            // ЭКРАН 2: Список расходов внутри конкретной категории
            composable(
                route = "expenses/{categoryId}",
                arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                ExpenseListScreen(
                    navController = navController,
                    categoryId = categoryId,
                    backStackEntry = backStackEntry
                )
            }

            // ЭКРАН 3: Список доходов
            composable("income") {
                IncomeListScreen(navController = navController)
            }
        }
    }
}