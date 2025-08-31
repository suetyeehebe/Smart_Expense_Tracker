package com.fit3163.myapplication

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.fit3163.myapplication.data.expenses.ExpensesViewModel
import com.fit3163.myapplication.ui.theme.ThemeMode
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

@Composable
fun MyNavHost(
    currentTheme: ThemeMode,
    onThemeChanged: (ThemeMode) -> Unit,
    navController: NavHostController,
    onSelectedChange: (String) -> Unit
) {
    //val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel() // Import androidx.lifecycle.viewmodel.compose.viewModel
    val expensesViewModel: ExpensesViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Safely update selected screen state
    LaunchedEffect(currentRoute) {
        currentRoute?.let { onSelectedChange(it) }
    }

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreenFunction(navController, authViewModel)
        }
        composable("signup") {
            SignupScreenFunction(navController,authViewModel)
        }
        composable("Settings") {
            //onSelectedChange("Settings")
            SettingScreen(
                navController = navController,
                authViewModel = authViewModel,
                currentTheme = currentTheme,
                onThemeChanged = onThemeChanged
            )
        }

        composable("account settings") {
            //onSelectedChange("Settings")
            AccountSettingScreen(navController)
        }

        composable("Analytics") {
            //onSelectedChange("Analytics")
            AnalyticsScreen(
                navController = navController,
                authViewModel = authViewModel,
                currentTheme = currentTheme,
                onThemeChanged = onThemeChanged
            )
        }

        composable("Expenses"){
            //onSelectedChange("Expenses")
            ExpensesScreen(navController, expensesViewModel)
        }

        composable(
            route = "ExpenseDetails/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id")
            ExpenseDetailScreen(navController, expensesViewModel, id)
        }

        composable("AddExpense"){
            ExpenseDetailScreen(navController, expensesViewModel, null)
        }

        composable("Budgets"){
            // onSelectedChange("Budgets")
            BudgetScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(navController: NavHostController, selected: String) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val cameraHelper = remember { CameraHelper(activity) }
    val taggunOcrHelper = remember { TaggunOcrHelper() }
    var scannedJson by remember { mutableStateOf("") }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = cameraHelper.getPhotoUri()
            if (uri != null) {
                val imageFile = uriToFile(context, uri)
                if (imageFile != null) {
                    Log.d("OCR", "OCR callback reached, sending to Firestore")
                    taggunOcrHelper.sendImageForOcr(imageFile) { jsonResult ->
                        scannedJson = jsonResult
                        saveJsonToDownloads(context, jsonResult)
                        saveJsonToFirestore(context, jsonResult)
                    }
                } else {
                    Toast.makeText(context, "Failed to read image", Toast.LENGTH_SHORT).show()
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

    NavigationBar {
        NavigationBarItem(
            selected = selected == "Analytics",
            onClick = {navController.navigate("Analytics")},
            icon = { Icon(Icons.Default.PieChart, contentDescription = "Analytics") },
            label = { Text("Analytics") }
        )

        NavigationBarItem(
            selected = selected == "Expenses",
            onClick = {navController.navigate("Expenses")},
            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Expenses") },
            label = { Text("Expenses") }
        )

        NavigationBarItem(
            selected = selected == "Add",
            onClick = {
                showSheet = true
                      },
            icon = { Icon(Icons.Default.Add, contentDescription = null) }
        )

        NavigationBarItem(
            selected = selected == "Budgets",
            onClick = {navController.navigate("Budgets")},
            icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Budgets") },
            label = { Text("Budgets") }
        )

        NavigationBarItem(
            selected = selected == "Settings",
            onClick = {navController.navigate("Settings")},
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )
    }

    if(showSheet){
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            modifier = Modifier.height(200.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    onClick = {
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                cameraLauncher.launch(cameraHelper.getCameraIntent())
                            }
                            else -> {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.DocumentScanner, contentDescription = "ScanReceipt")
                    Spacer(Modifier.width(6.dp))
                    Text("Scan Receipt")
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    onClick = {
                        navController.navigate("AddExpense")
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "ManualInput")
                    Spacer(Modifier.width(6.dp))
                    Text("Manual Input")
                }
            }
        }
    }
}

// Convert URI to File
fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        tempFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Save JSON to Downloads
fun saveJsonToDownloads(context: Context, json: String) {
    Log.d("FirestoreUpload", "saveJsonToFirestore triggered with JSON: $json") // Add this
    try {
        val fileName = "ocr_result_${System.currentTimeMillis()}.txt"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        FileOutputStream(file).use { it.write(json.toByteArray()) }
        Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
    }
}

// Save JSON to Firestore (safe version)
fun saveJsonToFirestore(context: Context, json: String) {
    Log.d("FirestoreUpload", "saveJsonToFirestore triggered with JSON: $json")

    val db = FirebaseFirestore.getInstance()
    try {
        val jsonObject = JSONObject(json)
        val totalAmount = jsonObject.optDouble("totalAmount", 0.0)
        val paidAmount = jsonObject.optDouble("paidAmount", 0.0)
        val date = jsonObject.optString("date", "")
        val dataMap = mapOf(
            "rawReceipt" to json,
            "totalAmount" to totalAmount,
            "paidAmount" to paidAmount,
            "date" to date
        )

        // Ensure Firestore runs on main thread
        (context as? Activity)?.runOnUiThread {
            db.collection("receipts")
                .add(dataMap)
                .addOnSuccessListener { doc ->
                    Log.d("FirestoreUpload", "Upload success! Doc ID: ${doc.id}")
                    Toast.makeText(context, "Uploaded to Firestore!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreUpload", "Upload failed", e)
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

    } catch (e: Exception) {
        Log.e("FirestoreUpload", "Error uploading JSON", e)
        Toast.makeText(context, "Failed to upload JSON", Toast.LENGTH_SHORT).show()
    }
}

fun Context.findActivity(): Activity {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    throw IllegalStateException("Activity not found from context")
}