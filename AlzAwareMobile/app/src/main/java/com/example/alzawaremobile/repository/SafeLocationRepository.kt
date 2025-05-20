package com.example.alzawaremobile.repository

import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.models.SafeLocation
import com.example.alzawaremobile.models.SafeLocationRequest
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SafeLocationRepository {
    private val apiService = ApiClient.createService(ApiService::class.java)

    fun addSafeLocation(
        request: SafeLocationRequest,
        callback: (Result<MessageResponse>) -> Unit
    ) {
        apiService.addSafeLocation(request).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Sunucu hatası: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
    fun getSafeLocationsByPatient(patientId: Long, callback: (Result<List<SafeLocation>>) -> Unit) {
        apiService.getSafeLocationsByPatient(patientId).enqueue(object : Callback<List<SafeLocation>> {
            override fun onResponse(call: Call<List<SafeLocation>>, response: Response<List<SafeLocation>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    } ?: callback(Result.failure(Exception("Boş veri")))
                } else {
                    callback(Result.failure(Exception("Kod: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<List<SafeLocation>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
    fun deleteSafeLocation(locationId: Long, callback: (Result<MessageResponse>) -> Unit) {
        apiService.deleteSafeLocation(locationId).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Silme hatası: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }


}
