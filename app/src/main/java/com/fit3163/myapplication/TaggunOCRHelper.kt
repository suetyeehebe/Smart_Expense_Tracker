package com.fit3163.myapplication

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.io.IOException

class TaggunOcrHelper {

    private val client = OkHttpClient()

    fun sendImageForOcr(imageFile: File, onResult: (String) -> Unit) {
        Log.d("OCR", "sendImageForOcr called with file: ${imageFile.absolutePath}")

        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                imageFile.name,
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
            )
            .build()

        val request = Request.Builder()
            .url("https://api.taggun.io/api/receipt/v1/verbose/file")
            .post(body)
            .addHeader("accept", "application/json")
            .addHeader("apikey", "2b2d786f3dee496b954e6d6a341d3151") // Replace with your key
            .build()

        Log.d("OCR", "Sending request to Taggun OCR API...")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OCR", "OCR request failed: ${e.message}", e)
                onResult("{\"error\":\"${e.message}\"}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() ?: "{}"
                Log.d("OCR", "Raw OCR Response: $responseBody")
                val cleanedJson = cleanJson(responseBody)
                Log.d("OCR", "Cleaned OCR JSON: $cleanedJson")
                onResult(cleanedJson)
            }
        })
    }

    private fun cleanJson(rawJson: String): String {
        return try {
            val json = JSONObject(rawJson)
            val cleanObject = JSONObject()

            // Get receipt full text
            val receiptText = when (val txt = json.opt("text")) {
                is JSONObject -> txt.optString("text", "")
                is String -> txt
                else -> ""
            }

            // --- Extract total amount (flexible) ---
            var totalAmount = 0.0
            // Try multiple patterns for total amount
            val totalPatterns = listOf(
                "Total\\s*(?:\\(MYR\\))?\\s*[:]?\\s*RM?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)", // Total RM 1,193.4
                "Amount\\s*[:]?\\s*RM?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)", // Amount: RM59.00 or Amount RM 1,193.4
                "Total\\s*[:]?\\s*([0-9,]+(?:\\.[0-9]{1,2})?)", // Total: 1,193.4
                "Amount\\s*([0-9,]+(?:\\.[0-9]{1,2})?)" // Amount 1,193.4
            )
            
            for (pattern in totalPatterns) {
                val regex = Regex(pattern, RegexOption.IGNORE_CASE)
                val match = regex.find(receiptText.replace("\n", " "))
                if (match != null) {
                    totalAmount = match.groupValues[1].replace(",", "").toDouble()
                    break
                }
            }

            // --- Extract date only ---
            var dateStr = ""
            // Try multiple date patterns
            val datePatterns = listOf(
                "Date\\s*[:]?\\s*(\\d{1,2}[/-]\\d{1,2}[/-]\\d{4})", // Date: 28/9/2025
                "Order Time\\s*[:]?\\s*(\\d{4}[-/]\\d{2}[-/]\\d{2})", // Order Time: 2074-04-30
                "(\\d{4}[-/]\\d{2}[-/]\\d{2})", // 2074-04-30
                "(\\d{1,2}[/-]\\d{1,2}[/-]\\d{4})", // 28/9/2025
                "(\\d{2}[-/]\\d{2}[-/]\\d{4})", // 28-04-2025
                "(\\d{4}\\s\\d{2}\\s\\d{2})" // 2024 04 30
            )
            
            for (pattern in datePatterns) {
                val regex = Regex(pattern, RegexOption.IGNORE_CASE)
                val match = regex.find(receiptText)
                if (match != null) {
                    dateStr = match.groupValues[1]
                    break
                }
            }

            // Put into cleaned object
            cleanObject.put("totalAmount", totalAmount)
            cleanObject.put("date", dateStr)
            cleanObject.put("text", receiptText)

            // Return pretty-printed JSON
            cleanObject.toString(4)
        } catch (e: Exception) {
            Log.e("OCR", "JSON cleaning failed: ${e.message}", e)
            "{\"error\":\"Failed to clean JSON: ${e.message}\"}"
        }
    }





}
