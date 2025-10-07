package com.fit3163.myapplication

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit3163.myapplication.data.budgets.Budget
import com.fit3163.myapplication.data.budgets.toDto
import com.fit3163.myapplication.data.expenses.Category
import com.fit3163.myapplication.data.expenses.ExpensesViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(expensesViewModel: ExpensesViewModel) {
    val expensesUi by expensesViewModel.ui.collectAsStateWithLifecycle()
    val expenses = expensesUi.items
    val budgets by expensesViewModel.budgets.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var editIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }
    var viewCategory: Budget? by remember { mutableStateOf(null) }

    // Firebase instance - this will be your placeholder
    val db = FirebaseFirestore.getInstance()

    // Function to save budget to Firebase
    fun saveBudgetToFirebase(budget: Budget) {
        println("🟢 DEBUG: saveBudgetToFirebase() CALLED with budget: ${budget.category ?: "Overall"} - RM ${budget.total}")

        // Remove the coroutineScope.launch and .await() completely
        db.collection("budgets")
            .document(budget.id)
            .set(budget.toDto())
            .addOnSuccessListener {
                println("SUCCESS: Budget saved to Firebase: ${budget.id}")
            }
            .addOnFailureListener { e ->
                println("ERROR saving budget to Firebase: ${e.message}")
                e.printStackTrace()
            }
    }

    fun deleteBudgetFromFirebase(budgetId: String) {
        println("🟢 DEBUG: deleteBudgetFromFirebase() CALLED with ID: $budgetId")

        // Remove the coroutineScope.launch and .await() completely
        db.collection("budgets")
            .document(budgetId)
            .delete()
            .addOnSuccessListener {
                println("SUCCESS: Budget deleted from Firebase: $budgetId")
            }
            .addOnFailureListener { e ->
                println("ERROR deleting budget from Firebase: ${e.message}")
                e.printStackTrace()
            }
    }

//    // Mock Firebase functions for testing
//    fun saveBudgetToFirebase(budget: Budget) {
//        println("[MOCK FIREBASE] Would save budget to Firebase:")
//        println("   - ID: ${budget.id}")
//        println("   - Category: ${budget.category ?: "Overall"}")
//        println("   - Total: RM ${budget.total}")
//        println("   - Type: ${budget.type}")
//        // You could also show a Snackbar to the user
//    }
//
//    fun deleteBudgetFromFirebase(budgetId: String) {
//        println("[MOCK FIREBASE] Would delete budget from Firebase: $budgetId")
//        // You could also show a Snackbar to the user
//    }


    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Budgets",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Budget")
                }
            }
        }
    ) { innerPadding ->

        if (budgets.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No budgets yet. Tap + to add one!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                itemsIndexed(budgets) { index, budget ->
                    val spent = budget.spent(expenses)
                    BudgetItem(
                        budget = budget,
                        spent = spent,
                        onEditPress = { editIndex = index },
                        onDeletePress = { showDeleteDialog = index },
                        onLongPress = { viewCategory = budget }
                    )
                }
            }
        }

        if (showAddDialog) {
            BudgetDialog(
                title = "Add Budget",
                initialBudget = null,
                onDismiss = { showAddDialog = false },
                onConfirm = { newBudget ->
                    println("🟢 DEBUG: Add Budget Dialog - onConfirm triggered")
                    expensesViewModel.addBudget(newBudget)
                    // Save to Firebase
                    saveBudgetToFirebase(newBudget)
                    showAddDialog = false
                }
            )
        }

        editIndex?.let { idx ->
            BudgetDialog(
                title = "Edit Budget",
                initialBudget = budgets[idx],
                onDismiss = { editIndex = null },
                onConfirm = { updatedBudget ->
                    println("🟢 DEBUG: Edit Budget Dialog - onConfirm triggered")
                    expensesViewModel.updateBudget(idx, updatedBudget)
                    // Update Firebase
                    saveBudgetToFirebase(updatedBudget)
                    editIndex = null
                }
            )
        }

        showDeleteDialog?.let { idx ->
            val budgetToDelete = budgets[idx]
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                confirmButton = {
                    TextButton(onClick = {
                        println("🟢 DEBUG: Delete Budget Dialog - Confirm button clicked")
                        expensesViewModel.deleteBudget(idx)
                        // Delete from Firebase
                        deleteBudgetFromFirebase(budgetToDelete.id)
                        showDeleteDialog = null
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        println("🟢 DEBUG: Delete Budget Dialog - Cancel button clicked")
                        showDeleteDialog = null }) { Text("Cancel") }
                },
                title = { Text("Delete Budget") },
                text = { Text("Are you sure you want to delete this budget?") }
            )
        }

        viewCategory?.let { budget ->
            val categoryExpenses = expenses.filter { it.category.label == budget.category }
            AlertDialog(
                onDismissRequest = { viewCategory = null },
                confirmButton = {
                    TextButton(onClick = { viewCategory = null }) { Text("Close") }
                },
                title = { Text("${budget.category} Expenses") },
                text = {
                    if (categoryExpenses.isEmpty()) {
                        Text("No expenses recorded for this category.")
                    } else {
                        Column {
                            categoryExpenses.forEach {
                                Text("RM ${it.amount} • ${it.date} • ${it.notes}")
                            }
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BudgetItem(
    budget: Budget,
    spent: Double,
    onEditPress: () -> Unit,
    onDeletePress: () -> Unit,
    onLongPress: () -> Unit
) {
    val progress = if (budget.total > 0) spent.toFloat() / budget.total.toFloat() else 0f
    val isCloseToMax = progress >= 0.9f
    val isOverBudget = progress >= 1f
    val progressColor = when {
        isOverBudget -> Color.Red
        isCloseToMax -> Color(0xFFFFA500)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = onLongPress
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    budget.category ?: "Overall",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(budget.type, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                if (isCloseToMax || isOverBudget) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Budget Warning",
                        tint = if (isOverBudget) Color.Red else Color(0xFFFFA500),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Text(
                "RM ${"%.2f".format(spent)} out of RM ${budget.total}",
                modifier = Modifier.padding(top = 4.dp)
            )

            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(top = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEditPress) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDeletePress) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDialog(
    title: String,
    initialBudget: Budget?,
    onDismiss: () -> Unit,
    onConfirm: (Budget) -> Unit
) {
    var category by remember { mutableStateOf(initialBudget?.category ?: Category.FOOD.label) }
    var total by remember { mutableStateOf(initialBudget?.total?.toString() ?: "") }
    var type by remember { mutableStateOf(initialBudget?.type ?: "Monthly") }
    var expanded by remember { mutableStateOf(false) }
    var isOverall by remember { mutableStateOf(initialBudget?.category == null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                // Toggle between Overall vs Category Budget
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { isOverall = true }) {
                        Text(
                            "Overall Budget",
                            color = if (isOverall) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    TextButton(onClick = { isOverall = false }) {
                        Text(
                            "Category Budget",
                            color = if (!isOverall) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }

                if (!isOverall) {
                    // Dropdown for category
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            Category.values().forEach { c ->
                                DropdownMenuItem(text = { Text(c.label) }, onClick = {
                                    category = c.label
                                    expanded = false
                                })
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = total,
                    onValueChange = { total = it },
                    label = { Text("Total Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row {
                    TextButton(onClick = { type = "Monthly" }) {
                        Text("Monthly", color = if (type == "Monthly") MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                    TextButton(onClick = { type = "Weekly" }) {
                        Text("Weekly", color = if (type == "Weekly") MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val totalInt = total.toIntOrNull() ?: initialBudget?.total ?: 0
                    val selectedCategory = if (isOverall) null else category

                    // FIX: Create Budget with proper parameters including ID
                    val newBudget = if (initialBudget != null) {
                        // Editing existing budget - keep the same ID
                        Budget(
                            id = initialBudget.id,
                            category = selectedCategory,
                            total = totalInt,
                            type = type
                        )
                    } else {
                        // Creating new budget - generate new ID
                        Budget(
                            category = selectedCategory,
                            total = totalInt,
                            type = type
                        )
                    }

                    onConfirm(newBudget)
                },
                enabled = total.toIntOrNull() != null
            ) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        properties = DialogProperties(dismissOnClickOutside = false)
    )
}