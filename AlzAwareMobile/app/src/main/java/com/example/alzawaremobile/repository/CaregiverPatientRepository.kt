package com.example.alzawaremobile.repository

import com.example.alzawaremobile.models.CaregiverPatientMatchRequest
import com.example.alzawaremobile.models.LocationDto
import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CaregiverPatientRepository {

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

    fun assignPatientToCaregiver(
        request: CaregiverPatientMatchRequest,
        callback: (Result<MessageResponse>) -> Unit
    ) {
        apiService.assignPatientToCaregiver(request).enqueue(callbackAdapter(callback))
    }

    fun getPatientsByCaregiver(caregiverId: Long, callback: (Result<List<User>>) -> Unit) {
        apiService.getPatientsByCaregiver(caregiverId).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback(Result.success(it)) }
                        ?: callback(Result.failure(Exception("Boş veri döndü")))
                } else {
                    callback(Result.failure(Exception("Kod: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun updateLocation(caregiverId: Long, latitude: Double, longitude: Double, callback: (Result<Void>) -> Unit) {
        val locationDto = LocationDto(latitude, longitude)
        apiService.updateLocation(caregiverId, locationDto).enqueue(callbackAdapter(callback))
    }

    fun getLocation(caregiverId: Long, callback: (Result<LocationDto>) -> Unit) {
        apiService.getLocation(caregiverId).enqueue(callbackAdapter(callback))
    }
}
