package com.example.expensetracker.data.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

fun getCurrentDateTime(): String {
    return Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .toString()
}