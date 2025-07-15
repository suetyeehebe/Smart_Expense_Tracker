package com.fit3163.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.fit3163.myapplication.ui.theme.ThemeMode
import com.google.firebase.auth.FirebaseAuth

class AnalyticScreen {
    @Composable
    fun AnalyticScreenFunction(
        navController: NavHostController,
        authViewModel: AuthViewModel,
        currentTheme: ThemeMode,
        onThemeChanged: (ThemeMode) -> Unit
    ){

        var showSheet by remember { mutableStateOf(false) }
        val authState = authViewModel.authState.observeAsState()
        val context = LocalContext.current
        val name = FirebaseAuth.getInstance().currentUser?.displayName

        LaunchedEffect(authState.value) {
            when (authState.value) {
                is AuthState.Unauthenticated -> navController.navigate("login")
                else -> Unit
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
//                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row(){
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Analytics",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        onClick = {
                            authViewModel.signout()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Logout", fontSize = 16.sp)
                    }
                }
            }

        }








    }
}

