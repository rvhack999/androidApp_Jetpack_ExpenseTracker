package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.data.model.TopBarScreen
import com.example.expensetracker.ui.screens.categories.CategoryListScreen
import com.example.expensetracker.ui.screens.expenses.ExpenseListScreen
import com.example.expensetracker.ui.screens.incoms.IncomeListScreen
import com.example.expensetracker.ui.theme.CustomTopBar
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.screens.mainScreen.MainScreen


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
                    CustomTopBar(currentScreen) { selectedScreen ->
                        when (selectedScreen) {
                            TopBarScreen.INCOME -> {
                                navController.navigate("income") {
                                    popUpTo("graphics") { saveState = true } // Меняем привязку на стартовый экран
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            TopBarScreen.EXPENSES -> {
                                navController.navigate("categories") {
                                    popUpTo("graphics") { saveState = true } // Меняем привязку на стартовый экран
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            TopBarScreen.GRAPHICS -> {
                                navController.navigate("graphics") {
                                    popUpTo("graphics") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                },
                // ИСПРАВЛЕНО: Убираем лишние системные отступы статус-бара, чтобы уменьшить размер топбара во всем приложении
                //windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "graphics", // Ваш новый стартовый экран дашборда
            modifier = Modifier
                .fillMaxSize()
                // ИСПРАВЛЕНО: Вместо ручных 60.dp передаем paddingValues от Scaffold,
                // чтобы контент автоматически и идеально вставал ровно под CustomTopBar
                .padding(top = paddingValues.calculateTopPadding() - 35.dp)

        ) {
            // КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ: Регистрируем маршрут "graphics" для вашего нового MainScreen дашборда
            composable("graphics") {
                MainScreen(navController = navController)
            }

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