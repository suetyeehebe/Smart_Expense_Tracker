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

//            loginScreen.LoginScreenFunction()
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val loginScreen = LoginScreen()
    val signupScreen = SignupScreen()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            loginScreen.LoginScreenFunction(navController)
        }
        composable("signup") {
            signupScreen.SignupScreenFunction(navController)
        }
    }
}


