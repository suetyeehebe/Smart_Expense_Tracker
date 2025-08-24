    package com.fit3163.myapplication

    import android.Manifest
    import android.app.Activity
    import android.content.Context
    import android.content.pm.PackageManager
    import android.net.Uri
    import android.os.Bundle
    import android.os.Environment
    import android.util.Log
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
    import com.google.firebase.firestore.FirebaseFirestore
    import org.json.JSONObject
    import java.io.File
    import java.io.FileOutputStream

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

                SmartExpenseTrackerTheme(themeMode = selectedTheme) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (selected != "login" && selected != "signup") {
                                BottomNavBar(navController, selected)
                            }
                        }
                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            MyNavHost(selectedTheme, { selectedTheme = it }, navController, { selected = it })

                            Button(
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
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text("Scan Receipt (Taggun OCR)")
                            }

                            if (scannedJson.isNotEmpty()) {
                                Text(
                                    text = "Taggun OCR JSON:\n$scannedJson",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
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

