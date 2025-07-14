package com.fit3163.myapplication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.fit3163.myapplication.ui.theme.ThemeMode

class SettingScreen {

    @Composable
    fun MainSettingScreenFunction(
        navController: NavHostController,
        authViewModel: AuthViewModel,
        currentTheme: ThemeMode,
        onThemeChanged: (ThemeMode) -> Unit
    ) {
        var showSheet by remember { mutableStateOf(false) }
        val authState = authViewModel.authState.observeAsState()
        val context = LocalContext.current

        LaunchedEffect(authState.value) {
            when (authState.value) {
                is AuthState.Unauthenticated -> navController.navigate("login")
                else -> Unit
            }
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
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
                            indicatorColor = Color.White,
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
                                contentDescription = "Account",
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        label = { Text("Account") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.White,
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

        // Bottom Sheet for Theme Selection
        if (showSheet) {
            AppearanceBottomSheet(
                selectedTheme = currentTheme,
                onDismiss = { showSheet = false },
                onThemeSelected = {
                    onThemeChanged(it)
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
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            listOf(
                ThemeMode.SYSTEM to "System Theme",
                ThemeMode.LIGHT to "Light Theme",
                ThemeMode.DARK to "Dark Theme"
            ).forEach { (mode, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onThemeSelected(mode) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedTheme == mode,
                        onClick = { onThemeSelected(mode) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = label, fontSize = 18.sp)
                }
            }
        }
    }
}
