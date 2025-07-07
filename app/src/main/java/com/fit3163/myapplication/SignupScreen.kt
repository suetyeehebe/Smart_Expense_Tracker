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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SignupScreen {
    @Composable
    fun SignupScreenFunction (){

        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

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
                "Sign up",
                fontSize = 24.sp,
                textAlign = TextAlign.Companion.Center,
                fontWeight = FontWeight.Companion.Bold
            )
            Spacer(modifier = Modifier.Companion.height(8.dp))
            Text("Stay updated on your financial expenses")

            Spacer(modifier = Modifier.Companion.height(32.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
        }

            Column (
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(top = 324.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Companion.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.Companion.height(32.dp))
                Button(onClick = { /* Handle login */ }) {
                    Text("Sign up")
                }

                Spacer(modifier = Modifier.Companion.height(8.dp))
                Text(
                    "Already have an account?",
                    modifier = Modifier.Companion.clickable {
                        // Navigate to SignUpScreen()
                    },
                    color = Color.Companion.Blue
                )
            }

        }
    }
