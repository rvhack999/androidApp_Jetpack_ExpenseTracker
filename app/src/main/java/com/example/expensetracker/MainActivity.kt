package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.ui.screens.CategoryListScreen
import com.example.expensetracker.ui.screens.ExpenseListScreen
import com.example.expensetracker.ui.screens.IncomeListScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "categories"
                    ) {
                        composable("categories") {
                            CategoryListScreen(navController = navController)
                        }

                        composable(
                            route = "expenses/{categoryId}",
                            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
                            ExpenseListScreen(
                                navController = navController,
                                categoryId = categoryId,
                                backStackEntry = backStackEntry  // ← ДОБАВИТЬ ЭТУ СТРОКУ
                            )
                        }

                        composable("income") {
                            IncomeListScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}