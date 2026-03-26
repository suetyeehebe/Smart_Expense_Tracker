package com.fit3163.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            // 🔹 Step 1: Keep track of theme mode
            var selectedTheme by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
            val navController: NavHostController = rememberNavController()
            var selected by remember { mutableStateOf("") }
            var showLoading by remember { mutableStateOf(false) }
            val authViewModel: AuthViewModel = viewModel()
            val bottomNavRoutes = listOf("Analytics", "Expenses", "Budgets", "Settings")

            SmartExpenseTrackerTheme(themeMode = selectedTheme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { if(selected in bottomNavRoutes) {
                        BottomNavBar(navController, selected, showLoading, { showLoading = it })
                    } }) { innerPadding ->
                        Column( modifier = Modifier.padding(innerPadding) ) {
                            MyNavHost(selectedTheme, { selectedTheme=it }, navController, {selected = it})
                        }
                    }
                    
                    // Global loading screen overlay - appears above everything
                    if (showLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.7f))
                                .zIndex(999f), // Ensure it appears above everything
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier.padding(32.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(48.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Processing Receipt...",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Please wait while we extract information from your receipt",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

