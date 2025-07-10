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
//import java.lang.reflect.Modifier

class AccountSettings {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AccountSettingScreenFunction(navController: NavHostController){

        var name by remember { mutableStateOf("") }
        var checked by remember { mutableStateOf(true) }

        Scaffold(
            containerColor = Color.White,
            bottomBar = {
                NavigationBar(containerColor = Color.White) {

                    NavigationBarItem(
                        selected = true,
                        onClick = { navController.navigate("main settings") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.setting_icon),
                                contentDescription = "Settings",
                                modifier = Modifier.size(32.dp)
                            )

                        },
                        label = { Text("Settings") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.White, // Set selected background to white
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    NavigationBarItem(
                        selected = true,
                        onClick = { navController.navigate("main settings") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.appearance_icon2),
                                contentDescription = "Settings",
                                modifier = Modifier.size(32.dp)
                            )

                        },
                        label = { Text("Account") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.White, // Set selected background to white
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        ) { innerPadding ->Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top

        ){
            Spacer(modifier = Modifier.height(40.dp))

            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable{navController.navigate("main settings")}

                )
                Spacer(modifier = Modifier.width(38.dp))
                Text("Account Settings", fontSize = 32.sp, fontWeight = FontWeight.Bold)

            }

            Text("qmah0001@student.monash.edu",fontSize = 24.sp,
                modifier = Modifier.
                align(Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(id = R.drawable.person_icon2),
                    contentDescription = "Account Icon",
                    modifier = Modifier.size(48.dp)
                )

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text("Name", fontSize = 24.sp) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent, // No background
                        unfocusedIndicatorColor = Color.Gray, // Underline color
                        focusedIndicatorColor = Color.Black,
                        disabledIndicatorColor = Color.LightGray
                    ),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 24.sp)
                )

            }
            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
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
                    onCheckedChange = { isChecked ->
                        checked = isChecked
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }









            }
        }
    }
}