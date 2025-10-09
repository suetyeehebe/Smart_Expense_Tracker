package com.fit3163.myapplication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults.colors
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    var name by remember { mutableStateOf(firebaseUser?.displayName ?: "") }
    val email = firebaseUser?.email ?: "No email found"
    var checked by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { navController.navigate("Settings") }
                )
                Spacer(modifier = Modifier.width(38.dp))
                Text("Account Settings", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }

            // Email (from Firebase)
            Text(
                email,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Editable Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.person_icon2),
                    contentDescription = "Account Icon",
                    modifier = Modifier.size(48.dp)
                )

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", fontSize = 24.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Gray,
                        focusedIndicatorColor = Color.Black
                    ),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 24.sp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button for name update
            androidx.compose.material3.Button(
                onClick = {
                    val user = FirebaseAuth.getInstance().currentUser
                    val updates = com.google.firebase.auth.userProfileChangeRequest {
                        displayName = name
                    }
                    user?.updateProfile(updates)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Save Changes")
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Notifications toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.notification_icon2),
                    contentDescription = "Notification Icon",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text("Notifications", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(100.dp))
                Switch(
                    checked = checked,
                    onCheckedChange = { isChecked -> checked = isChecked },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
