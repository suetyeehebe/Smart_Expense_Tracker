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
    //val merchant: String,
    val date: LocalDate,
    val amount: Double,
    val category: Category,
    val notes: String = "",
    val receiptUrl: String? = null
)