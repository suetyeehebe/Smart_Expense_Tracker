package com.fit3163.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fit3163.myapplication.ui.theme.SmartExpenseTrackerTheme
import com.fit3163.myapplication.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 🔹 Step 1: Keep track of theme mode
            var selectedTheme by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
            val navController: NavHostController = rememberNavController()
            var selected by remember { mutableStateOf("") }
            val authViewModel: AuthViewModel = viewModel()
            val bottomNavRoutes = listOf("Analytics", "Expenses", "Budgets", "Settings")

            SmartExpenseTrackerTheme(themeMode = selectedTheme) {
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { if(selected in bottomNavRoutes) {
                    BottomNavBar(navController, selected)
                } }) { innerPadding ->
                    Column( modifier = Modifier.padding(innerPadding) ) {
                        MyNavHost(selectedTheme, { selectedTheme=it }, navController, {selected = it})
                    }
                }
            }
        }
    }
}

