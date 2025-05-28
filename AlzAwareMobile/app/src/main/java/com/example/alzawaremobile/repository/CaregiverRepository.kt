package com.example.alzawaremobile.repository

import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CaregiverRepository {

    private val apiService: ApiService = ApiClient.createService(ApiService::class.java)

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

    fun getCaregiverProfile(
        caregiverId: Long,
        callback: (Result<User>) -> Unit
    ) {
        apiService.getCaregiverProfile(caregiverId).enqueue(callbackAdapter(callback))
    }

    fun updateCaregiverProfile(
        caregiverId: Long,
        updatedUser: User,
        callback: (Result<MessageResponse>) -> Unit
    ) {
        apiService.updateCaregiverProfile(caregiverId, updatedUser).enqueue(callbackAdapter(callback))
    }
}
