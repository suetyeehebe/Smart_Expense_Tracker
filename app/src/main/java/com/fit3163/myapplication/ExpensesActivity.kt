package com.fit3163.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fit3163.myapplication.ui.theme.SmartExpenseTrackerTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


data class Expense(
    val id: String,
    val merchant: String,
    val date: String,
    val amount: Double,
    val category: String
)

@Composable
fun ExpensesScreen(){
    val sampleExpenses = listOf(
        Expense("1", "McDonald's", "01/06/2025", 12.99, "Food & Drinks"),
        Expense("2", "Grab", "02/07/2025", 8.50, "Transport"),
        Expense("3", "Lazada", "02/07/2025", 110.00, "Shopping"),
        Expense("4", "Tealive", "04/07/2025", 10.5, "Food & Drinks")
    )

    var expanded by remember { mutableStateOf(false) }
    var expenses = sampleExpenses
    var selectedMonth by remember { mutableStateOf(YearMonth.of(2025, 8)) }
    // Filter expenses for the selected month
    val filteredExpenses = expenses.filter { expense ->
        val expenseDate = LocalDate.parse(expense.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        YearMonth.from(expenseDate) == selectedMonth
    }

    val groupedExpenses = filteredExpenses.groupBy { it.date }

    Column(modifier = Modifier.padding(16.dp)) {

        Row(){
            Text("Expenses", fontWeight = FontWeight.Bold, fontSize = 24.sp)

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

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
            }
            Text(
                text = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            IconButton(onClick = { selectedMonth = selectedMonth.plusMonths(1) }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
            }
        }

        LazyColumn {
//            items(expenses) { expense ->
//                ExpenseCard(expense)
//                Spacer(modifier = Modifier.height(8.dp))
//            }
            groupedExpenses.forEach { (date, expenses) ->
                item {
                    Text(date, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                }

                items(expenses) { expense ->
                    ExpenseItem(expense)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
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
                Spacer(Modifier.width(12.dp))
                Text(expense.category, fontSize = 16.sp)
            }

            Text(
                "RM%.2f".format(expense.amount),
                fontWeight = FontWeight.Bold
            )
        }
    }
}