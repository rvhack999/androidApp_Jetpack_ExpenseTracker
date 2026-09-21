package com.example.expensetracker.data.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

fun getCurrentDateTime(): String {
    return java.time.LocalDateTime.now().toString()
}