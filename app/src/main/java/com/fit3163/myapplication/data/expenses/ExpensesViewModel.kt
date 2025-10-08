package com.fit3163.myapplication.data.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit3163.myapplication.data.budgets.Budget
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class ExpensesUiState(
    val month: YearMonth = YearMonth.now(),
    val items: List<Expense> = emptyList()
)

class ExpensesViewModel : ViewModel() {

    private val _all = MutableStateFlow<List<Expense>>(emptyList())
    val allItems: StateFlow<List<Expense>> = _all.asStateFlow()

    private val month = MutableStateFlow(YearMonth.now())

    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    val ui: StateFlow<ExpensesUiState> =
        combine(_all, month) { items, m ->
            val filtered = items.filter { YearMonth.from(it.date) == m }
                .sortedByDescending { it.date }
            ExpensesUiState(m, filtered)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExpensesUiState())

    fun setMonth(m: YearMonth) {
        month.value = m
    }

    fun byId(id: String) = _all.value.find { it.id == id }

    fun upsert(expense: Expense) {
        _all.value = _all.value.toMutableList().apply {
            val i = indexOfFirst { it.id == expense.id }
            if (i >= 0) set(i, expense) else add(0, expense)
        }
    }

    fun delete(id: String) {
        _all.value = _all.value.filterNot { it.id == id }
    }

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

    /** ✅ Load Budgets from Firebase **/
    fun loadBudgetsFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        db.collection("budgets")
            .get()
            .addOnSuccessListener { result ->
                val loadedBudgets = result.mapNotNull { doc ->
                    try {
                        Budget(
                            id = doc.getString("id") ?: "",
                            category = doc.getString("category"),
                            total = (doc.getLong("total") ?: 0L).toInt(),
                            type = doc.getString("type") ?: "Monthly"
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                _budgets.value = loadedBudgets
                println("✅ Loaded ${loadedBudgets.size} budgets from Firestore")
            }
            .addOnFailureListener { e ->
                println("❌ Error loading budgets: ${e.message}")
            }
    }

    /** ✅ Load Expenses from Firebase **/
    fun loadExpensesFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        db.collectionGroup("expenses") // read from all subcollections named "expenses"
            .get()
            .addOnSuccessListener { result ->
                val loadedExpenses = result.mapNotNull { doc ->
                    try {
                        doc.toObject(ExpenseDto::class.java).toDomain()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                _all.value = loadedExpenses
                println("✅ Loaded ${loadedExpenses.size} expenses from Firestore")
            }
            .addOnFailureListener { e ->
                println("❌ Error loading expenses: ${e.message}")
            }
    }

    init {
        loadBudgetsFromFirebase()
        loadExpensesFromFirebase() // 👈 now this automatically loads your Firestore expenses
    }
}
