package com.fit3163.myapplication

import com.fit3163.myapplication.BuildConfig
import com.fit3163.myapplication.data.receipts.APIService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private val client by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    fun categorizerApi(
        baseUrl: String = BuildConfig.CAT_API_BASE
    ): APIService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl) // must end with "/"
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(APIService::class.java)
    }
}