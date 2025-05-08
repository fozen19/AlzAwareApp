package com.example.alzawaremobile.repository

import com.example.alzawaremobile.models.AuthResponse
import com.example.alzawaremobile.models.LoginRequest
import com.example.alzawaremobile.models.SignupRequest
import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {

    private val apiService: ApiService = ApiClient.createService(ApiService::class.java)

    fun signup(request: SignupRequest, callback: (Result<MessageResponse>) -> Unit) {
        apiService.signup(request).enqueue(callbackAdapter(callback))
    }

    fun login(request: LoginRequest, callback: (Result<AuthResponse>) -> Unit) {
        apiService.login(request).enqueue(callbackAdapter(callback))
    }

    private fun <T> callbackAdapter(callback: (Result<T>) -> Unit): Callback<T> {
        return object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    } ?: callback(Result.failure(Exception("Empty response body")))
                } else {
                    callback(Result.failure(Exception("Request failed with code ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback(Result.failure(t))
            }
        }
    }
}