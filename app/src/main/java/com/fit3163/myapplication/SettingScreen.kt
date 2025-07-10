package com.fit3163.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


class SettingScreen {
    @Composable
    fun MainSettingScreenFunction(navController: NavHostController) {
        var showSheet by remember { mutableStateOf(false) }
        var selectedTheme by remember { mutableStateOf("System Theme") }


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
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text("Settings", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(40.dp))

                // Account Settings Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("account settings") }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.person_icon2),
                        contentDescription = "Account Icon",
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Account Settings", fontSize = 24.sp, modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Appearance Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSheet = true }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.appearance_icon2),
                        contentDescription = "Appearance Icon",
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Appearance", fontSize = 24.sp, modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))

                // Logout Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = androidx.compose.ui.graphics.Color.Black
                        )
                    ) {
                        Text("Logout", fontSize = 16.sp)
                    }
                }
            }
        }

        // Bottom Sheet for Theme Selection
        if (showSheet) {
            AppearanceBottomSheet(
                selectedTheme = selectedTheme,
                onDismiss = { showSheet = false },
                onThemeSelected = {
                    selectedTheme = it
                    showSheet = false
                }
            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceBottomSheet(
    onDismiss: () -> Unit,
    selectedTheme: String,
    onThemeSelected: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            listOf("System Theme", "Light Theme", "Dark Theme").forEach { theme ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onThemeSelected(theme) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = theme == selectedTheme,
                        onClick = { onThemeSelected(theme) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = theme, fontSize = 18.sp)
                }
            }
        }
    }
}
