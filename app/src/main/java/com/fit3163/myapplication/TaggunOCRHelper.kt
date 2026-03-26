package com.fit3163.myapplication

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Purpose:
 * Sends a receipt image file to the Taggun OCR API endpoint,
 * receives the verbose JSON response, and returns a simplified,
 * cleaned JSON containing only the fields required downstream
 * (date, totalAmount, and text).
 */
class TaggunOcrHelper {
    private val client = OkHttpClient()
    /**
     * Function: sendImageForOcr
     * Uploads an image file to Taggun OCR and returns cleaned JSON via callback.
     * @param imageFile The receipt image captured or selected by the user.
     * @param onResult  Lambda callback that receives the JSON string result.
     */
    fun sendImageForOcr(imageFile: File, onResult: (String) -> Unit) {
        Log.d("OCR", "sendImageForOcr called with file: ${imageFile.absolutePath}")

        // 1) Build multipart body for image upload
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", imageFile.name, RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile))
            .build()

        // 2) Build request to Taggun verbose endpoint
        val request = Request.Builder()
            .url("https://api.taggun.io/api/receipt/v1/verbose/file")
            .post(body)
            .addHeader("accept", "application/json")
            .addHeader("apikey", "89b1b40028cc11f1866249fa1e600b38") // API key
            .build()

        Log.d("OCR", "Sending request to Taggun OCR API...")

        // 3) Execute request asynchronously to avoid blocking UI thread
        client.newCall(request).enqueue(object : Callback {
            /** Called when request fails */
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OCR", "OCR request failed: ${e.message}", e)
                //onResult("{\"error\":\"${e.message}\"}")
                Handler(Looper.getMainLooper()).post {
                    onResult("{\"error\":\"${e.message}\"}")
                }
            }
            /** Called when OCR request succeeds; parse and clean response JSON. */
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() ?: "{}"
                Log.d("OCR", "Raw OCR Response: $responseBody")

                try {
                    val json = JSONObject(responseBody)
                    // Extract structured fields  (total amount, date)
                    val totalAmount = json.optJSONObject("totalAmount")?.optDouble("data", 0.0) ?: 0.0
                    var dateStr = json.optJSONObject("date")?.optString("data", "") ?: ""

                    // Format date from ISO → MM/dd/yyyy for UI readability
                    if (dateStr.isNotEmpty()) {
                        try {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                            val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                            val parsedDate = inputFormat.parse(dateStr)
                            if (parsedDate != null) { dateStr = outputFormat.format(parsedDate) }
                        } catch (e: Exception) { /* Keep raw date if parsing fails */ }
                    }
                    // Extract full receipt text
                    val text = when (val t = json.opt("text")) {
                        is JSONObject -> t.optString("text", "")
                        is String -> t
                        else -> ""
                    }
                    // Build the cleaned JSON we pass downstream
                    val cleaned = JSONObject()
                        .put("totalAmount", totalAmount)
                        .put("date", dateStr)
                        .put("text", text)
                        .toString(4)
                    // Return cleaned JSON to callback
                    //onResult(cleaned)
                    Handler(Looper.getMainLooper()).post {
                        onResult(cleaned)
                    }
                } catch (e: Exception) {
                    Log.e("OCR", "Failed to parse OCR response: ${e.message}", e)
                    //onResult("{\"error\":\"${e.message}\"}")
                    Handler(Looper.getMainLooper()).post {
                        onResult("{\"error\":\"${e.message}\"}")
                    }
                }
            }
        })
    }

//    private fun cleanJson(json: JSONObject, totalAmount: Double, dateStr: String): String {
//        return try {
//            val cleanObject = JSONObject()
//
//            // Get receipt full text
//            val receiptText = when (val txt = json.opt("text")) {
//                is JSONObject -> txt.optString("text", "")
//                is String -> txt
//                else -> ""
//            }
//
//            // Put into cleaned object
//            cleanObject.put("totalAmount", totalAmount)
//            cleanObject.put("date", dateStr)
//            cleanObject.put("text", receiptText)
//
//            // Return pretty-printed JSON
//            cleanObject.toString(4)
//        } catch (e: Exception) {
//            Log.e("OCR", "JSON cleaning failed: ${e.message}", e)
//            "{\"error\":\"Failed to clean JSON: ${e.message}\"}"
//        }
//    }
}
