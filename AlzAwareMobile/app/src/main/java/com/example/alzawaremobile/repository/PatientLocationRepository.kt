package com.example.alzawaremobile.repository

import com.example.alzawaremobile.models.LocationDto
import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.models.PatientLocationResponse
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientLocationRepository {

    private val apiService: ApiService = ApiClient.createService(ApiService::class.java)

    fun updatePatientLocation(
        patientId: Long,
        locationDto: LocationDto,
        callback: (Result<MessageResponse>) -> Unit
    ) {
        apiService.updatePatientLocation(patientId, locationDto).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    } ?: callback(Result.failure(Exception("Empty response")))
                } else {
                    callback(Result.failure(Exception("Error code: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun getLatestPatientLocation(patientId: Long, callback: (Result<PatientLocationResponse>) -> Unit) {
        apiService.getPatientLatestLocation(patientId).enqueue(object : Callback<PatientLocationResponse> {
            override fun onResponse(call: Call<PatientLocationResponse>, response: Response<PatientLocationResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    } ?: callback(Result.failure(Exception("Empty response body")))
                } else {
                    callback(Result.failure(Exception("Code: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<PatientLocationResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

}
