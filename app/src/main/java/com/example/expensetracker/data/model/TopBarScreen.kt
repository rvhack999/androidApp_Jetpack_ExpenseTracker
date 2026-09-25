package com.example.expensetracker.data.model

enum class TopBarScreen(val title: String, val isActiveScreen: Boolean = false) {
    INCOME("Доходы"),
    EXPENSES("Расходы"),
    GRAPHICS("Графики")
}