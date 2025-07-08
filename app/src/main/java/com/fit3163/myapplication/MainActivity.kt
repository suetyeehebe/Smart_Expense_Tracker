package com.fit3163.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            SettingScreen().MainSettingScreen()
//            loginScreen.LoginScreenFunction()
            MyApp()

        }
    }

    @Composable
    fun MyApp() {
        val navController = rememberNavController()
        val loginScreen = LoginScreen()
        val signupScreen = SignupScreen()
        val settingScreen = SettingScreen()
        val accountSettingScreen = AccountSettings()

        NavHost(navController, startDestination = "main settings") {
//            composable("login") {
//                loginScreen.LoginScreenFunction(navController)
//            }
//            composable("signup") {
//                signupScreen.SignupScreenFunction(navController)
//            }
            composable("main settings"){
                settingScreen.MainSettingScreenFunction(navController)
            }
            composable("account settings"){
                accountSettingScreen.AccountSettingScreenFunction(navController)
            }
        }
    }
}



