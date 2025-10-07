package com.fit3163.myapplication

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.compose.rememberNavController
import com.fit3163.myapplication.data.expenses.Category
import com.fit3163.myapplication.data.expenses.Expense
import com.fit3163.myapplication.data.expenses.ExpensesViewModel
import com.fit3163.myapplication.data.expenses.toDto
import com.fit3163.myapplication.ui.theme.SmartExpenseTrackerTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun ExpensesScreen(navController: NavHostController, expensesViewModel: ExpensesViewModel){
//    val sampleExpenses = listOf(
//        Expense("1", "McDonald's", "01/06/2025", 12.99, "Food & Drinks"),
//        Expense("2", "Grab", "02/07/2025", 8.50, "Transport"),
//        Expense("3", "Lazada", "02/07/2025", 110.00, "Shopping"),
//        Expense("4", "Tealive", "04/07/2025", 10.5, "Food & Drinks")
//    )
    val sampleExpenses = listOf(
        Expense(
            id = "1",
            date = LocalDate.of(2025, 4, 30),
            category = Category.FOOD,
            amount = 20.0,
            notes = "Lunch with friends"
        ),
        Expense(
            id = "2",
            date = LocalDate.of(2025, 4, 30),
            category = Category.ENTERTAINMENT,
            amount = 20.0,
            notes = "Movie tickets"
        ),
        Expense(
            id = "3",
            date = LocalDate.of(2025, 4, 28),
            category = Category.GROCERIES,
            amount = 60.0,
            notes = "Weekly shopping"
        ),
        Expense(
            id = "4",
            date = LocalDate.of(2025, 4, 15),
            category = Category.TRANSPORT,
            amount = 10.0,
            notes = "Bus fare"
        ),
        Expense(
            id = "5",
            date = LocalDate.of(2025, 3, 15),
            category = Category.FOOD,
            amount = 15.0,
            notes = "Coffee break"
        )
    )

    //var expanded by remember { mutableStateOf(false) }
    var expenses = sampleExpenses
    var selectedMonth by remember { mutableStateOf(YearMonth.of(2025, 8)) }
    // Filter expenses for the selected month
    val filteredExpenses = expenses.filter { expense ->
        //val expenseDate = LocalDate.parse(expense.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        YearMonth.from(expense.date) == selectedMonth
    }

    //val groupedExpenses = filteredExpenses.groupBy { it.date }
    val expensesUi by expensesViewModel.ui.collectAsStateWithLifecycle()
    val groupedExpenses = remember(expensesUi.items) {expensesUi.items.groupBy { it.date }}

    Column(modifier = Modifier.padding(16.dp)) {

        Row(){
            Text("Expenses", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)

//            ExposedDropdownMenuBox(
//                expanded = expanded,
//                onExpandedChange = { expanded = it }
//            ) {
//                OutlinedTextField(
//                    modifier = Modifier.menuAnchor().height(54.dp).fillMaxWidth(),
//                    value = persona.value,
//                    onValueChange = { persona.value = it },
//                    readOnly = true,
//                    placeholder = { Text("Select option", fontWeight = FontWeight.Bold) },
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
//                )
//
//                ExposedDropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false }
//                ) {
//                    personaOptions.forEach { option ->
//                        DropdownMenuItem(
//                            text = { Text(option.name) },
//                            onClick = {
//                                persona.value = option.name
//                                expanded = false
//                            }
//                        )
//                    }
//                }
//            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
//            IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
//                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
//            }
            IconButton(onClick = { expensesViewModel.setMonth(expensesUi.month.minusMonths(1)) }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
            }
            Text(
               // text = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                text = expensesUi.month.label(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
//            IconButton(onClick = { selectedMonth = selectedMonth.plusMonths(1) }) {
//                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
//            }
            IconButton(onClick = { expensesViewModel.setMonth(expensesUi.month.plusMonths(1)) }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn {
//            items(expenses) { expense ->
//                ExpenseCard(expense)
//                Spacer(modifier = Modifier.height(8.dp))
//            }
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

@Composable
fun ExpenseItem(expense: Expense, navController: NavHostController) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().clickable {
//            navController.currentBackStackEntry
//                ?.savedStateHandle
//                ?.set("expenseId", expense.id)
            navController.navigate("ExpenseDetails/${expense.id}") },
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
//                Icon(
//                    imageVector = categoryIcon(expense.category),
//                    contentDescription = expense.category,
//                    modifier = Modifier.size(24.dp)
//                )
                Text(expense.category.icon)
                Spacer(Modifier.width(6.dp))
                Text(expense.category.label, fontSize = 16.sp)
            }

            Text(
                "RM%.2f".format(expense.amount),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailScreen(navController: NavHostController, expensesViewModel: ExpensesViewModel, expenseId: String?) {
    // Fetch expense from repository or dummy data
    //val expense = Expense(expenseId, "Food & Drinks", 20.0, "30/04/2025", "Dinner with friends")
//    val expenseId = navController.previousBackStackEntry
//        ?.savedStateHandle
//        ?.get<String>("expenseId")

//    val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
//    val expenseId = remember { savedStateHandle?.get<String>("expenseId") }

//    if (expenseId.isNullOrEmpty()) {
//        // You can show a snackbar, then pop
//        LaunchedEffect(Unit) { navController.popBackStack() }
//        return
//    }

    val oriExpense = remember(expenseId) { expenseId?.let { expensesViewModel.byId(it) } }
    //var amount by remember { mutableStateOf(oriExpense?.amount.toString()) }
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
                    Text("Expense", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        //context.startActivity(Intent(context, LoginActivity::class.java))
                        //navController.navigate("Expenses")
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    Text("Done", modifier = Modifier.padding(16.dp).clickable {
                        val parsed = amount.toDoubleOrNull()

                        if (parsed == null || parsed <= 0.0) {
                            amountError = true
                            return@clickable
                        }

                        val updated = (oriExpense ?: Expense(date = date, category = category, amount = parsed))
                            .copy(
                                amount = parsed,
                                category = category,
                                date = date,
                                notes = notes
                            )
                        val db = FirebaseFirestore.getInstance()
                        db.collection("dates")
                            .document(updated.date.toString()) // e.g. "2025-10-06"
                            .collection("categories")
                            .document(updated.category.toString())
                            .collection("expenses")
                            .document(updated.id)
                            .set(updated.toDto())


                            .addOnSuccessListener {
                                Log.d("Firestore", "Expense saved successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error saving expense", e)
                            }
                        expensesViewModel.upsert(updated)
                        //navController.navigate("Expenses")
                        navController.popBackStack()
                    })
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxSize()) {
            // Receipt placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Receipt Image", color = Color.DarkGray)
            }

            Spacer(Modifier.height(16.dp))

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

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = category.label,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Select Category") },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    Category.values().forEach { c ->
                        DropdownMenuItem(text = { Text(c.label) }, onClick = { category = c; expanded = false })
                    }
                }
            }

            Spacer(Modifier.height(8.dp))


            // Date picker
            //val context = LocalContext.current
            //val datePickerDialog = DatePickerFun(date)

            // Date (opens DatePickerDialog)
            val datePickerDialog = DatePickerFun(mDate = remember { mutableStateOf(date) }.also {
                // keep local state in sync with `date`
                LaunchedEffect(date) { if (it.value != date) it.value = date }
            }.apply {
                // when picker returns new value, copy into `date`
                val holder = this
                LaunchedEffect(holder.value) { date = holder.value }
            })

            Box(modifier = Modifier.fillMaxWidth()) {
                // The visible TextField
                OutlinedTextField(
                    value = date.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Transparent clickable layer above the TextField
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            indication = null, // no double ripple
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
                placeholder = {Text("Write a note")},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if(oriExpense != null){
                // Delete Button
                Button(
                    onClick = {
                        expensesViewModel.delete(oriExpense.id)
                        navController.popBackStack()
                              },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                    Text("Delete", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DatePickerFun(mDate: MutableState<LocalDate>): DatePickerDialog {
    val mContext = LocalContext.current

    val mYear: Int
    val mMonth: Int
    val mDay: Int

    val mCalendar = Calendar.getInstance()

    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    return DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = LocalDate.of(mYear, mMonth + 1, mDayOfMonth)
        }, mYear, mMonth, mDay
    )
}

private fun YearMonth.label(): String =
    month.name.lowercase().replaceFirstChar { it.titlecase() } + " " + year