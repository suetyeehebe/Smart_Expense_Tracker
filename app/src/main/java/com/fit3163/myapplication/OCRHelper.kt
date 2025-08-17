package com.fit3163.myapplication

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OCRHelper(private val context: Context) {

    fun runTextRecognition(imageUri: Uri, onResult: (String) -> Unit) {
        val image = InputImage.fromFilePath(context, imageUri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val resultText = visionText.text
                onResult(resultText)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "OCR Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
