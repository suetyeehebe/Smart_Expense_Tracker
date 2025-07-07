package com.fit3163.myapplication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class LoginScreen {
    @Composable
    fun LoginScreenFunction() {

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(top = 208.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {

            Text(
                "App Name",
                fontSize = 24.sp,
                textAlign = TextAlign.Companion.Center,
                fontWeight = FontWeight.Companion.Bold
            )
            Spacer(modifier = Modifier.Companion.height(8.dp))
            Text("Welcome to App Name!")
        }
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "Sign in",
                fontSize = 24.sp,
                textAlign = TextAlign.Companion.Center,
                fontWeight = FontWeight.Companion.Bold
            )
            Spacer(modifier = Modifier.Companion.height(8.dp))
            Text("Stay updated on your financial expenses")

            Spacer(modifier = Modifier.Companion.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth())


            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val iconText = if (passwordVisible) "Hide" else "Show"
                    Text(
                        text = iconText,
                        color = Color.Blue,
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    )
                }
            )


            Spacer(modifier = Modifier.Companion.height(16.dp))
        }


        Column (
            modifier = Modifier.Companion
            .fillMaxSize()
            .padding(top = 324.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ){
            Button(onClick = { /* Handle login */ }) {
                Text("Sign in")
            }
            Spacer(modifier = Modifier.Companion.height(8.dp))
            Text(
                "Don't have an account?",
                modifier = Modifier.Companion.clickable {
                    // Navigate to SignUpScreen()
                },
                color = Color.Companion.Blue
            )
        }

        }
    }
