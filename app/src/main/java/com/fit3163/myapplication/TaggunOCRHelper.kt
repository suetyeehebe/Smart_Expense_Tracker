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
            .addHeader("apikey", "a6f3824a3a5c499699e3af7035de153d") // Replace with your key
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

            // Handle totalAmount
            cleanObject.put(
                "totalAmount",
                when (val ta = json.opt("totalAmount")) {
                    is JSONObject -> ta.optDouble("data", 0.0)
                    is Number -> ta.toDouble()
                    else -> 0.0
                }
            )

            // Handle paidAmount
            cleanObject.put(
                "paidAmount",
                when (val pa = json.opt("paidAmount")) {
                    is JSONObject -> pa.optDouble("data", 0.0)
                    is Number -> pa.toDouble()
                    else -> 0.0
                }
            )

            // Handle change amount from amounts array if exists
            val changeAmount = json.optJSONArray("amounts")?.let { amounts ->
                var change: Double? = null
                for (i in 0 until amounts.length()) {
                    val obj = amounts.getJSONObject(i)
                    if (obj.optString("text").contains("Change", ignoreCase = true)) {
                        change = when (val data = obj.opt("data")) {
                            is Number -> data.toDouble()
                            else -> 0.0
                        }
                        break
                    }
                }
                change ?: 0.0
            } ?: 0.0
            cleanObject.put("change", changeAmount)

            cleanObject.put(
                "date",
                when (val dt = json.opt("date")) {
                    is JSONObject -> dt.optString("data", "")
                    is String -> dt
                    else -> ""
                }
            )
            cleanObject.put(
                "text",
                when (val txt = json.opt("text")) {
                    is JSONObject -> txt.optString("text", "")
                    is String -> txt
                    else -> ""
                }
            )

            // Return pretty-printed JSON
            cleanObject.toString(4)
        } catch (e: Exception) {
            Log.e("OCR", "JSON cleaning failed: ${e.message}", e)
            "{\"error\":\"Failed to clean JSON: ${e.message}\"}"
        }
    }
}
