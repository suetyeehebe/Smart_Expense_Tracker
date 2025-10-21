package com.fit3163.myapplication.data.receipts

import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class PredictRequest(
    val text: String
)

data class TopItem(
    val label: String,
    val confidence: Double
)

data class PredictResponse(
    val label: String,
    val confidence: Double,
    val top3: List<TopItem>
)

interface APIService {
    @POST("predict")
    suspend fun predict(@Body req: PredictRequest): PredictResponse
}