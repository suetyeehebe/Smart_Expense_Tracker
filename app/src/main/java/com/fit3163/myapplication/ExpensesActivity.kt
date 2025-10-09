package com.fit3163.myapplication

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.fit3163.myapplication.data.expenses.Category
import com.fit3163.myapplication.data.expenses.Expense
import com.fit3163.myapplication.data.expenses.ExpensesViewModel
import com.fit3163.myapplication.data.expenses.toDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.time.LocalDate
import java.time.YearMonth

// ====================== MAIN EXPENSES SCREEN ==========================
@Composable
fun ExpensesScreen(navController: NavHostController, expensesViewModel: ExpensesViewModel) {
    val expensesUi by expensesViewModel.ui.collectAsStateWithLifecycle()
    val groupedExpenses = remember(expensesUi.items) { expensesUi.items.groupBy { it.date } }

    Column(modifier = Modifier.padding(16.dp)) {

        // Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Expenses",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Month Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { expensesViewModel.setMonth(expensesUi.month.minusMonths(1)) }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
            }
            Text(
                text = expensesUi.month.label(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            IconButton(onClick = { expensesViewModel.setMonth(expensesUi.month.plusMonths(1)) }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
            }
        }

        Spacer(Modifier.height(8.dp))

        // Expense List
        LazyColumn {
            groupedExpenses.forEach { (date, expenses) ->
                item {
                    Text(date.toString(), fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                }
                items(expenses) { expense ->
                    ExpenseItem(expense, navController)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

// ====================== EXPENSE ITEM CARD ==========================
@Composable
fun ExpenseItem(expense: Expense, navController: NavHostController) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("ExpenseDetails/${expense.id}") },
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(expense.category.icon)
                Spacer(Modifier.width(6.dp))
                Text(expense.category.label, fontSize = 16.sp)
            }

            Text("RM%.2f".format(expense.amount), fontWeight = FontWeight.Bold)
        }
    }
}

// ====================== ADD / EDIT EXPENSE ==========================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(
    navController: NavHostController,
    expensesViewModel: ExpensesViewModel,
    expenseId: String?
) {
    val oriExpense = remember(expenseId) { expenseId?.let { expensesViewModel.byId(it) } }

    var amount by remember { mutableStateOf(oriExpense?.amount?.toString().orEmpty()) }
    var category by remember { mutableStateOf(oriExpense?.category ?: Category.OTHER) }
    var date by remember { mutableStateOf(oriExpense?.date ?: LocalDate.now()) }
    var notes by remember { mutableStateOf(oriExpense?.notes ?: "") }
    var amountError by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (expenseId == null) "Add Expense" else "Edit Expense",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Text(
                        "Done",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                val parsed = amount.toDoubleOrNull()

                                if (parsed == null || parsed <= 0.0) {
                                    amountError = true
                                    return@clickable
                                }

                                val updated = (oriExpense ?: Expense(
                                    date = date,
                                    category = category,
                                    amount = parsed
                                )).copy(
                                    amount = parsed,
                                    category = category,
                                    date = date,
                                    notes = notes
                                )
                                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@clickable
                                val db = FirebaseFirestore.getInstance()

// 🔹 Reference to the current date document
                                val dateRef = db.collection("users")
                                    .document(uid)
                                    .collection("dates")
                                    .document(updated.date.toString())

// ✅ Ensure the "date" document exists (creates if missing)
                                dateRef.set(mapOf("date" to updated.date.toString()), SetOptions.merge())

// 🔹 Reference to the category document under that date
                                val categoryRef = dateRef
                                    .collection("categories")
                                    .document(updated.category.toString())

// ✅ Ensure the "category" document exists (creates if missing)
                                categoryRef.set(mapOf("category" to updated.category.toString()), SetOptions.merge())

// 🔹 Now save the expense inside the category's "expenses" subcollection
                                categoryRef
                                    .collection("expenses")
                                    .document(updated.id)
                                    .set(updated.toDto())
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "✅ Expense saved successfully to Firestore")

                                        // 👇 Refresh expenses after saving
                                        expensesViewModel.loadExpensesFromFirebase()

                                        // 👇 Update the local state as well
                                        expensesViewModel.upsert(updated)

                                        // 👇 Navigate back
                                        navController.popBackStack()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firestore", "❌ Error saving expense", e)
                                    }

                            }
                    )

                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                    amountError = false
                },
                label = { Text("Amount") },
                placeholder = { Text("0") },
                singleLine = true,
                isError = amountError,
                supportingText = {
                    if (amountError) Text("Please enter a valid amount greater than 0")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Category
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = category.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Category.values().forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.label) },
                            onClick = {
                                category = c
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Date Picker
            val mDate = remember { mutableStateOf(date) }
            val datePickerDialog = rememberDatePickerDialog(mDate)
            LaunchedEffect(mDate.value) { date = mDate.value }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = date.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { datePickerDialog.show() }
                )
            }

            Spacer(Modifier.height(8.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                placeholder = { Text("Write a note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Delete Button (only if editing)
            if (oriExpense != null) {
                Button(
                    onClick = {
                        expensesViewModel.delete(oriExpense.id)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Delete", color = Color.White)
                }
            }
        }
    }
}

// ====================== DATE PICKER ==========================
@Composable
fun rememberDatePickerDialog(mDate: MutableState<LocalDate>): DatePickerDialog {
    val mContext = LocalContext.current
    val mCalendar = Calendar.getInstance()

    val mYear = mCalendar.get(Calendar.YEAR)
    val mMonth = mCalendar.get(Calendar.MONTH)
    val mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    return remember {
        DatePickerDialog(
            mContext,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                mDate.value = LocalDate.of(year, month + 1, day)
            },
            mYear,
            mMonth,
            mDay
        )
    }
}

// ====================== UTIL ==========================
private fun YearMonth.label(): String =
    month.name.lowercase().replaceFirstChar { it.titlecase() } + " " + year
