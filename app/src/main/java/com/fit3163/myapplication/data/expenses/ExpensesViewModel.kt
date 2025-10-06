package com.fit3163.myapplication.data.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit3163.myapplication.data.budgets.Budget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import kotlin.random.Random

data class ExpensesUiState(
    val month: YearMonth = YearMonth.now(),
    val items: List<Expense> = emptyList()
)

class ExpensesViewModel : ViewModel() {
    val expenses_list = seed()
    // pretend repository feed
    private val all = MutableStateFlow(expenses_list)
    val allItems: StateFlow<List<Expense>> = all.asStateFlow()

    private val month = MutableStateFlow(YearMonth.now())

    private val _budgets = MutableStateFlow<List<Budget>>(emptyList()) // added
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    val ui: StateFlow<ExpensesUiState> =
        combine(all, month) { items, m ->
            val filtered = items.filter { YearMonth.from(it.date) == m }
                .sortedByDescending { it.date }
            ExpensesUiState(m, filtered)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExpensesUiState())

    fun setMonth(m: YearMonth) { month.value = m }

    fun byId(id: String) = all.value.find { it.id == id }

    fun upsert(expense: Expense) {
        all.value = all.value.toMutableList().apply {
            val i = indexOfFirst { it.id == expense.id }
            if (i >= 0) set(i, expense) else add(0, expense)
        }
    }

    fun delete(id: String) { all.value = all.value.filterNot { it.id == id } }

    fun addBudget(budget: Budget) {
        _budgets.value = _budgets.value + budget
    }

    fun updateBudget(index: Int, updated: Budget) {
        _budgets.value = _budgets.value.toMutableList().apply {
            set(index, updated)
        }
    }

    fun deleteBudget(index: Int) {
        _budgets.value = _budgets.value.toMutableList().apply {
            removeAt(index)
        }
    }
}

private fun seed(): List<Expense> {
    val now = LocalDate.now()
    val cats = Category.values()
    return List(22) {
        val d = now.minusDays(Random.nextInt(0, 20).toLong())
        val c = cats.random()
        val amt = (6..120).random().toDouble()
        Expense(date = d, category = c, amount = amt, notes = listOf("", "Lunch", "Grab", "Movie").random())
    }.sortedByDescending { it.date }
}