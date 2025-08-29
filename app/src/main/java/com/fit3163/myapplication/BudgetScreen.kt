//package com.fit3163.myapplication
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material3.*
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Card
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.DialogProperties
//
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.runtime.Composable
//
//@Preview(showBackground = true)
//@Composable
//fun BudgetScreenPreview() {
//    BudgetScreen()
//}
//
//@Composable
//fun BudgetScreen() {
//    var budgets by remember {
//        mutableStateOf(
//            mutableListOf(
//                Budget("Groceries", 200, 500, "Monthly"),
//                Budget("Transport", 60, 100, "Weekly"),
//                Budget("Entertainment", 150, 300, "Monthly")
//            )
//        )
//    }
//
//    var showAddDialog by remember { mutableStateOf(false) }
//    var editIndex by remember { mutableStateOf<Int?>(null) }
//    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }
//
//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(onClick = { showAddDialog = true }) {
//                Icon(Icons.Default.Add, contentDescription = "Add Budget")
//            }
//        }
//    ) { padding ->
//        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
//            Text(
//                text = "Budgets",
//                style = MaterialTheme.typography.headlineMedium,
//                modifier = Modifier.padding(16.dp)
//            )
//
//            LazyColumn(modifier = Modifier.fillMaxSize()) {
//                itemsIndexed(budgets) { index, budget ->
//                    BudgetItem(
//                        budget = budget,
//                        onLongPress = { editIndex = index },
//                        onDeletePress = { showDeleteDialog = index }
//                    )
//                }
//            }
//        }
//    }
//
//    // Add Budget Dialog
//    if (showAddDialog) {
//        BudgetDialog(
//            title = "Add Budget",
//            initialBudget = null,
//            onDismiss = { showAddDialog = false },
//            onConfirm = { newBudget ->
//                budgets = (budgets + newBudget).toMutableList()
//                showAddDialog = false
//            }
//        )
//    }
//
//    // Edit Budget Dialog
//    editIndex?.let { idx ->
//        BudgetDialog(
//            title = "Edit Budget",
//            initialBudget = budgets[idx],
//            onDismiss = { editIndex = null },
//            onConfirm = { updatedBudget ->
//                budgets[idx] = updatedBudget.copy(spent = budgets[idx].spent) // keep spent
//                budgets = budgets.toMutableList()
//                editIndex = null
//            }
//        )
//    }
//
//    // Delete confirmation
//    showDeleteDialog?.let { idx ->
//        AlertDialog(
//            onDismissRequest = { showDeleteDialog = null },
//            confirmButton = {
//                TextButton(onClick = {
//                    budgets = budgets.toMutableList().also { it.removeAt(idx) }
//                    showDeleteDialog = null
//                }) {
//                    Text("Delete")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDeleteDialog = null }) {
//                    Text("Cancel")
//                }
//            },
//            title = { Text("Delete Budget") },
//            text = { Text("Are you sure you want to delete this budget?") }
//        )
//    }
//}
//
//@Composable
//fun BudgetItem(
//    budget: Budget,
//    onLongPress: () -> Unit,
//    onDeletePress: () -> Unit
//) {
//    var showWarning by remember { mutableStateOf(false) }
//    val progress = budget.spent.toFloat() / budget.total.toFloat()
//
//    // Check warning conditions
//    LaunchedEffect(budget.spent, budget.total) {
//        when {
//            budget.spent >= budget.total -> showWarning = true
//            budget.spent >= 0.9 * budget.total -> showWarning = true
//        }
//    }
//
//    if (showWarning) {
//        AlertDialog(
//            onDismissRequest = { showWarning = false },
//            confirmButton = {
//                TextButton(onClick = { showWarning = false }) {
//                    Text("OK")
//                }
//            },
//            title = {
//                Text(if (budget.spent >= budget.total) "Budget Limit Reached" else "Budget Almost Reached")
//            },
//            text = {
//                Text(
//                    if (budget.spent >= budget.total)
//                        "You’ve reached your budget for ${budget.category}!"
//                    else
//                        "You’ve used up 90% of your ${budget.category} budget."
//                )
//            }
//        )
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .clickable(onClick = onLongPress),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column(modifier = Modifier.padding(12.dp)) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(budget.category, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(0.7f))
//                Text(budget.type, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.3f), color = Color.Gray)
//            }
//
//            Text("RM ${budget.spent} out of RM ${budget.total}", modifier = Modifier.padding(top = 4.dp))
//
//            LinearProgressIndicator(
//                progress = progress.coerceIn(0f, 1f),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(8.dp)
//                    .padding(top = 4.dp)
//            )
//
//            IconButton(
//                onClick = onDeletePress,
//                modifier = Modifier.align(Alignment.End)
//            ) {
//                Icon(Icons.Default.Delete, contentDescription = "Delete")
//            }
//        }
//    }
//}
//
//@Composable
//fun BudgetDialog(
//    title: String,
//    initialBudget: Budget?,
//    onDismiss: () -> Unit,
//    onConfirm: (Budget) -> Unit
//) {
//    var category by remember { mutableStateOf(initialBudget?.category ?: "") }
//    var total by remember { mutableStateOf(initialBudget?.total?.toString() ?: "") }
//    var type by remember { mutableStateOf(initialBudget?.type ?: "Monthly") }
//
//    AlertDialog(
//        onDismissRequest = { onDismiss() },
//        title = { Text(title) },
//        text = {
//            Column {
//                OutlinedTextField(
//                    value = category,
//                    onValueChange = { category = it },
//                    label = { Text("Category") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                OutlinedTextField(
//                    value = total,
//                    onValueChange = { total = it },
//                    label = { Text("Total Amount") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Row {
//                    TextButton(onClick = { type = "Monthly" }) {
//                        Text("Monthly", color = if (type == "Monthly") MaterialTheme.colorScheme.primary else Color.Unspecified)
//                    }
//                    TextButton(onClick = { type = "Weekly" }) {
//                        Text("Weekly", color = if (type == "Weekly") MaterialTheme.colorScheme.primary else Color.Unspecified)
//                    }
//                }
//            }
//        },
//        confirmButton = {
//            TextButton(onClick = {
//                val totalInt = total.toIntOrNull() ?: initialBudget?.total ?: 0
//                onConfirm(Budget(category, initialBudget?.spent ?: 0, totalInt, type))
//            }) {
//                Text("Save")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) { Text("Cancel") }
//        },
//        properties = DialogProperties(dismissOnClickOutside = false)
//    )
//}


package com.fit3163.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    BudgetScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen() {
    var budgets by remember {
        mutableStateOf(
            mutableListOf(
                Budget("Groceries", 200, 500, "Monthly"),
                Budget("Transport", 60, 100, "Weekly"),
                Budget("Entertainment", 150, 300, "Monthly")
            )
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var editIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            Text(
                text = "Budgets",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Budget")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            itemsIndexed(budgets) { index, budget ->
                BudgetItem(
                    budget = budget,
                    onLongPress = { editIndex = index },
                    onDeletePress = { showDeleteDialog = index }
                )
            }
        }
    }

    // Add Budget Dialog
    if (showAddDialog) {
        BudgetDialog(
            title = "Add Budget",
            initialBudget = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { newBudget ->
                budgets = budgets.toMutableList().apply { add(newBudget) }
                showAddDialog = false
            }
        )
    }

    // Edit Budget Dialog
    editIndex?.let { idx ->
        BudgetDialog(
            title = "Edit Budget",
            initialBudget = budgets[idx],
            onDismiss = { editIndex = null },
            onConfirm = { updatedBudget ->
                budgets = budgets.toMutableList().apply {
                    // Keep the original spent amount
                    set(idx, updatedBudget.copy(spent = budgets[idx].spent))
                }
                editIndex = null
            }
        )
    }

    // Delete confirmation
    showDeleteDialog?.let { idx ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            confirmButton = {
                TextButton(onClick = {
                    budgets = budgets.toMutableList().apply { removeAt(idx) }
                    showDeleteDialog = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Budget") },
            text = { Text("Are you sure you want to delete this budget?") }
        )
    }
}

@Composable
fun BudgetItem(
    budget: Budget,
    onLongPress: () -> Unit,
    onDeletePress: () -> Unit
) {
    val progress = budget.spent.toFloat() / budget.total.toFloat()
    val isCloseToMax = progress >= 0.9f
    val isOverBudget = progress >= 1f

    // Determine progress bar color
    val progressColor = when {
        isOverBudget -> Color.Red
        isCloseToMax -> Color(0xFFFFA500) // Orange color for warning
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onLongPress),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    budget.category,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    budget.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                // Warning icon for budgets close to or over limit
                if (isCloseToMax || isOverBudget) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Budget Warning",
                        tint = if (isOverBudget) Color.Red else Color(0xFFFFA500),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Text("RM ${budget.spent} out of RM ${budget.total}", modifier = Modifier.padding(top = 4.dp))

            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(top = 4.dp)
            )

            IconButton(
                onClick = onDeletePress,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun BudgetDialog(
    title: String,
    initialBudget: Budget?,
    onDismiss: () -> Unit,
    onConfirm: (Budget) -> Unit
) {
    var category by remember { mutableStateOf(initialBudget?.category ?: "") }
    var total by remember { mutableStateOf(initialBudget?.total?.toString() ?: "") }
    var type by remember { mutableStateOf(initialBudget?.type ?: "Monthly") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
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
                    onConfirm(Budget(category, initialBudget?.spent ?: 0, totalInt, type))
                },
                enabled = category.isNotBlank() && total.toIntOrNull() != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        properties = DialogProperties(dismissOnClickOutside = false)
    )
}