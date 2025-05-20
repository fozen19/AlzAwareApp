package com.example.alzawaremobile.repository

import com.example.alzawaremobile.models.Medicine
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicineRepository {

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

    fun getMedicinesByPatient(patientId: Long, callback: (Result<List<Medicine>>) -> Unit) {
        apiService.getMedicinesByPatient(patientId).enqueue(callbackAdapter(callback))
    }

    fun createMedicine(medicine: Medicine, callback: (Result<Medicine>) -> Unit) {
        apiService.createMedicine(medicine).enqueue(callbackAdapter(callback))
    }

    fun getMedicineById(id: Long, callback: (Result<Medicine>) -> Unit) {
        apiService.getMedicineById(id).enqueue(callbackAdapter(callback))
    }

    fun updateMedicine(id: Long, medicine: Medicine, callback: (Result<Medicine>) -> Unit) {
        apiService.updateMedicine(id, medicine).enqueue(callbackAdapter(callback))
    }

    fun deleteMedicine(id: Long, callback: (Result<Unit>) -> Unit) {
        apiService.deleteMedicine(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback(Result.success(Unit))
                } else {
                    callback(Result.failure(Exception("Request failed with code ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}
