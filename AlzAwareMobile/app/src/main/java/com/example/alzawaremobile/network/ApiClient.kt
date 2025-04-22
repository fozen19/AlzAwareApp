package com.example.alzawaremobile.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://your-backend-url.com/" // ← kendi backend URL’ini yaz

    // Token'lı Retrofit oluştur
    private fun getRetrofit(token: String? = null): Retrofit {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Token'sız varsayılan kullanım
    val apiService: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }

    // Token'lı API servisi
    fun getApiService(token: String? = null): ApiService {
        return getRetrofit(token).create(ApiService::class.java)
    }
}
