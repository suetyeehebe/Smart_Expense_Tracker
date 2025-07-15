package com.fit3163.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

            // 🔹 Step 2: Wrap entire app in the theme
            SmartExpenseTrackerTheme(themeMode = selectedTheme) {
                MyApp(
                    currentTheme = selectedTheme,
                    onThemeChanged = { selectedTheme = it }
                )
            }
        }
    }

    @Composable
    fun MyApp(
        currentTheme: ThemeMode,
        onThemeChanged: (ThemeMode) -> Unit
    ) {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = viewModel() // Import androidx.lifecycle.viewmodel.compose.viewModel

        val loginScreen = LoginScreen()
        val signupScreen = SignupScreen()
        val settingScreen = SettingScreen()
        val accountSettingScreen = AccountSettings()
        val analyticScreen = AnalyticScreen()

        NavHost(navController, startDestination = "analytics") {
            composable("login") {
                loginScreen.LoginScreenFunction(navController, authViewModel)
            }
            composable("signup") {
                signupScreen.SignupScreenFunction(navController,authViewModel)
            }
            composable("main settings") {

                settingScreen.MainSettingScreenFunction(
                    navController = navController,
                    authViewModel = authViewModel,
                    currentTheme = currentTheme,
                    onThemeChanged = onThemeChanged
                )
            }

            composable("account settings") {
                accountSettingScreen.AccountSettingScreenFunction(navController)
            }

            composable("analytics") {
                analyticScreen.AnalyticScreenFunction(
                    navController = navController,
                    authViewModel = authViewModel,
                    currentTheme = currentTheme,
                    onThemeChanged = onThemeChanged
                )
            }
        }
    }

    // Optional: if you want to use this bottom nav bar later
    @Composable
    fun BottomNavBar(currentDestination: String, onSettingsClick: () -> Unit) {
        NavigationBar(containerColor = Color.White) {
            NavigationBarItem(
                selected = currentDestination == "settings",
                onClick = onSettingsClick,
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                label = { Text("Settings") },
                alwaysShowLabel = true
            )
        }
    }
}




