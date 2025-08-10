package com.fit3163.myapplication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
            ExpensesScreen()
        }

        composable("Budgets"){
            // onSelectedChange("Budgets")

        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController, selected: String) {
    val items = listOf("Analytics", "Expenses", "Budgets", "Settings")

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
            onClick = {},
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
}