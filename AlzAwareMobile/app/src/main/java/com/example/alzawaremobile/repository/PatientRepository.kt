package com.example.alzawaremobile.repository

import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientRepository {

    private val apiService: ApiService = ApiClient.createService(ApiService::class.java)

    private fun <T> callbackAdapter(callback: (Result<T>) -> Unit): Callback<T> {
        return object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    } ?: callback(Result.failure(Exception("Boş yanıt içeriği")))
                } else {
                    callback(Result.failure(Exception("Sunucu hatası: Kod ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback(Result.failure(t))
            }
        }
    }

    fun getPatientById(patientId: Long, callback: (Result<User>) -> Unit) {
        apiService.getPatientProfile(patientId).enqueue(callbackAdapter(callback))
    }

    fun updatePatient(patientId: Long, updatedUser: User, callback: (Result<MessageResponse>) -> Unit) {
        apiService.updatePatientProfile(patientId, updatedUser).enqueue(callbackAdapter(callback))
    }
}
