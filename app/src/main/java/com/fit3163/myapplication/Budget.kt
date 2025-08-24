package com.fit3163.myapplication

data class Budget(
    val category: String,
    val spent: Int,
    val total: Int,
    val type: String  // e.g., "Month" or "Week"
)