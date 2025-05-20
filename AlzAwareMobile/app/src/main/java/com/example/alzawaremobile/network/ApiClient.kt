package com.example.alzawaremobile.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://172.21.251.80:8080/" // Replace with your actual base URL

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}