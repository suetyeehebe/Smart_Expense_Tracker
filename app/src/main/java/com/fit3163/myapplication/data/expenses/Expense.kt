package com.fit3163.myapplication.data.expenses

import java.time.LocalDate
import java.util.UUID

enum class Category(val label: String, val icon: String) {
    FOOD("Food & Drinks", "🍔"),
    ENTERTAINMENT("Entertainment", "🎳"),
    GROCERIES("Groceries", "🛒"),
    TRANSPORT("Transport", "🚆"),
    HOME("Home", "🏠"),
    WEARABLES("Wearables", "👕"),
    BEAUTY("Beauty", "💄"),
    EDUCATION("Education", "🎓"),
    HEALTHCARE("Healthcare", "🏥"),
    OTHER("Other", "➕")
}

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate = LocalDate.now(),
    val amount: Double = 0.0,
    val category: Category = Category.OTHER,
    val notes: String = "",
    val receiptUrl: String? = null
)

data class ExpenseDto(
    val id: String = "",
    val date: String = "",        // store as String
    val amount: Double = 0.0,
    val category: String = "",    // store as String
    val notes: String = "",
    val receiptUrl: String? = null
)


fun Expense.toDto(): ExpenseDto = ExpenseDto(
    id = id,
    date = date.toString(),          // LocalDate → String
    amount = amount,
    category = category.name,        // Enum → String
    notes = notes,
    receiptUrl = receiptUrl
)

fun ExpenseDto.toDomain(): Expense = Expense(
    id = id,
    date = LocalDate.parse(date),    // String → LocalDate
    amount = amount,
    category = Category.valueOf(category), // String → Enum
    notes = notes,
    receiptUrl = receiptUrl
)
