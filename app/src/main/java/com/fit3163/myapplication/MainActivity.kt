package com.fit3163.myapplication

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fit3163.myapplication.ui.theme.SmartExpenseTrackerTheme
import com.fit3163.myapplication.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var selectedTheme by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
            val navController: NavHostController = rememberNavController()
            var selected by remember { mutableStateOf("") }
            val authViewModel: AuthViewModel = viewModel()

            val context = this
            val cameraHelper = remember { CameraHelper(this) }
            val ocrHelper = remember { OCRHelper(this) }
            var scannedText by remember { mutableStateOf("") }

            // Camera launcher
            val cameraLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = cameraHelper.getPhotoUri()
                    if (uri != null) {
                        ocrHelper.runTextRecognition(uri) { text ->
                            scannedText = text
                            Toast.makeText(context, "OCR Done!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Camera cancelled", Toast.LENGTH_SHORT).show()
                }
            }

            // Permission launcher
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    cameraLauncher.launch(cameraHelper.getCameraIntent())
                } else {
                    Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            SmartExpenseTrackerTheme(themeMode = selectedTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (selected != "login" && selected != "signup") {
                            BottomNavBar(navController, selected)
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        MyNavHost(selectedTheme, { selectedTheme = it }, navController, { selected = it })

                        // OCR Button with permission check
                        Button(onClick = {
                            when {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    // Already granted → launch camera
                                    cameraLauncher.launch(cameraHelper.getCameraIntent())
                                }
                                else -> {
                                    // Ask for permission
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        }, modifier = Modifier.padding(16.dp)) {
                            Text("Scan Receipt (OCR)")
                        }

                        if (scannedText.isNotEmpty()) {
                            Text(
                                text = "Scanned Text:\n$scannedText",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
