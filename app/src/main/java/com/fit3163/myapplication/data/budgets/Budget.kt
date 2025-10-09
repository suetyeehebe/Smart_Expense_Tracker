package com.fit3163.myapplication.data.budgets

import com.fit3163.myapplication.data.expenses.Expense
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import java.util.UUID

data class Budget(
    val id: String = UUID.randomUUID().toString(),
    val category: String? = null,
    val total: Int = 0,
    val type: String = "Monthly"
) {
    fun spent(expenses: List<Expense>): Double {
        val filtered = when {
            category == null -> expenses
            else -> expenses.filter { it.category.label == category }
        }

        return when (type) {
            "Weekly" -> filtered.filter { it.date.isInThisWeek() }
            else -> filtered.filter { it.date.isInThisMonth() }
        }.sumOf { it.amount }
    }
}

fun LocalDate.isInThisMonth(): Boolean {
    val now = LocalDate.now()
    return year == now.year && month == now.month
}

fun LocalDate.isInThisWeek(): Boolean {
    val now = LocalDate.now()
    val weekFields = WeekFields.of(Locale.getDefault())
    return get(weekFields.weekOfWeekBasedYear()) == now.get(weekFields.weekOfWeekBasedYear()) &&
            year == now.year
}