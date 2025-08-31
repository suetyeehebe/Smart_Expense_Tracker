package com.fit3163.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.fit3163.myapplication.data.expenses.ExpensesViewModel
import com.fit3163.myapplication.ui.theme.ThemeMode

@Composable
fun MyNavHost(
    currentTheme: ThemeMode,
    onThemeChanged: (ThemeMode) -> Unit,
    navController: NavHostController,
    onSelectedChange: (String) -> Unit
) {
    //val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel() // Import androidx.lifecycle.viewmodel.compose.viewModel
    val expensesViewModel: ExpensesViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Safely update selected screen state
    LaunchedEffect(currentRoute) {
        currentRoute?.let { onSelectedChange(it) }
    }

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreenFunction(navController, authViewModel)
        }
        composable("signup") {
            SignupScreenFunction(navController,authViewModel)
        }
        composable("Settings") {
            //onSelectedChange("Settings")
            SettingScreen(
                navController = navController,
                authViewModel = authViewModel,
                currentTheme = currentTheme,
                onThemeChanged = onThemeChanged
            )
        }

        composable("account settings") {
            //onSelectedChange("Settings")
            AccountSettingScreen(navController)
        }

        composable("Analytics") {
            //onSelectedChange("Analytics")
            AnalyticsScreen(
                navController = navController,
                authViewModel = authViewModel,
                currentTheme = currentTheme,
                onThemeChanged = onThemeChanged
            )
        }

        composable("Expenses"){
            //onSelectedChange("Expenses")
            ExpensesScreen(navController, expensesViewModel)
        }

//        composable("ExpenseDetails/${id}"){ backStack ->
//            val id = backStack.arguments?.getString("id")
//            ExpenseDetailScreen(navController, expensesViewModel, id)
//        }
        composable(
            route = "ExpenseDetails/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id")
            ExpenseDetailScreen(navController, expensesViewModel, id)
        }

        composable("AddExpense"){
            ExpenseDetailScreen(navController, expensesViewModel, null)
        }

        composable("Budgets"){
            // onSelectedChange("Budgets")
            BudgetScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(navController: NavHostController, selected: String) {
    val items = listOf("Analytics", "Expenses", "Budgets", "Settings")
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    NavigationBar {
        NavigationBarItem(
            selected = selected == "Analytics",
            onClick = {navController.navigate("Analytics")},
            icon = { Icon(Icons.Default.PieChart, contentDescription = "Analytics") },
            label = { Text("Analytics") }
        )

        NavigationBarItem(
            selected = selected == "Expenses",
            onClick = {navController.navigate("Expenses")},
            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Expenses") },
            label = { Text("Expenses") }
        )

        NavigationBarItem(
            selected = selected == "Add",
            onClick = {
                showSheet = true
                      },
            icon = { Icon(Icons.Default.Add, contentDescription = null) }
        )

        NavigationBarItem(
            selected = selected == "Budgets",
            onClick = {navController.navigate("Budgets")},
            icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Budgets") },
            label = { Text("Budgets") }
        )

        NavigationBarItem(
            selected = selected == "Settings",
            onClick = {navController.navigate("Settings")},
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )
    }

    if(showSheet){
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            modifier = Modifier.height(200.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    onClick = {

                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.DocumentScanner, contentDescription = "ScanReceipt")
                    Spacer(Modifier.width(6.dp))
                    Text("Scan Receipt")
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    onClick = {
                        navController.navigate("AddExpense")
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "ManualInput")
                    Spacer(Modifier.width(6.dp))
                    Text("Manual Input")
                }
            }
        }
    }
}