package com.fit3163.myapplication.data.expenses

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit3163.myapplication.data.budgets.Budget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("budgets")
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
                println("✅ Loaded ${loadedBudgets.size} budgets for user $uid")
            }
            .addOnFailureListener { e ->
                println("❌ Error loading budgets: ${e.message}")
            }
    }

    /** ✅ Load Expenses from Firebase (correct structure) **/
    fun loadExpensesFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        println("🔄 Loading expenses for user: $uid")
        db.collection("users")
            .document(uid)
            .collection("dates")
            .get()
            .addOnSuccessListener { dateDocs ->
                if (dateDocs.isEmpty) {
                    println("📅 No dates found for user $uid")
                    _all.value = emptyList()
                    return@addOnSuccessListener
                }

                val allExpenses = mutableListOf<Expense>()
                var remainingDates = dateDocs.size()

                for (dateDoc in dateDocs) {
                    val dateId = dateDoc.id
                    db.collection("users")
                        .document(uid)
                        .collection("dates")
                        .document(dateId)
                        .collection("categories")
                        .get()
                        .addOnSuccessListener { categoryDocs ->
                            var remainingCategories = categoryDocs.size()

                            for (categoryDoc in categoryDocs) {
                                val categoryId = categoryDoc.id
                                db.collection("users")
                                    .document(uid)
                                    .collection("dates")
                                    .document(dateId)
                                    .collection("categories")
                                    .document(categoryId)
                                    .collection("expenses")
                                    .get()
                                    .addOnSuccessListener { expenseDocs ->
                                        val expenses = expenseDocs.mapNotNull { expenseDoc ->
                                            try {
                                                expenseDoc.toObject(ExpenseDto::class.java).toDomain()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                null
                                            }
                                        }

                                        allExpenses.addAll(expenses)
                                        println("✅ Loaded ${expenses.size} expenses under $dateId/$categoryId")

                                        remainingCategories--
                                        if (remainingCategories == 0) {
                                            remainingDates--
                                            if (remainingDates == 0) {
                                                _all.value = allExpenses
                                                println("🎯 Finished loading: ${allExpenses.size} total expenses")
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        println("❌ Error loading expenses in $dateId/$categoryId: ${e.message}")
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            println("❌ Error loading categories for $dateId: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                println("❌ Error loading dates: ${e.message}")
            }
    }




    init {
        val auth = FirebaseAuth.getInstance()

        // 🔄 Automatically reload when login/logout happens
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                println("👤 User logged in: ${user.uid}")
                loadBudgetsFromFirebase()
                loadExpensesFromFirebase()
            } else {
                println("🚪 User logged out — clearing local data")
                _all.value = emptyList()
                _budgets.value = emptyList()
            }
        }
    }

}
