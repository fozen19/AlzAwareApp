package com.example.alzawaremobile.repository

import android.util.Log
import com.example.alzawaremobile.models.GeofenceRequest
import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GeofenceRepository {

    // Assuming ApiClient.retrofit correctly initializes Retrofit with GsonConverterFactory
    private val apiService: ApiService = ApiClient.retrofit.create(ApiService::class.java)

    fun saveGeofence(
        token: String, // Pass the auth token
        geofenceRequest: GeofenceRequest,
        onResult: (Result<MessageResponse>) -> Unit
    ) {
        // Assuming your token needs to be prefixed with "Bearer "
        // Adjust if your auth scheme is different
        val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

        apiService.saveGeofence(geofenceRequest).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.i("GeofenceRepository", "Geofence saved successfully: ${it.message}")
                        onResult(Result.success(it))
                    } ?: run {
                        // This case might indicate a 2xx response with an empty body,
                        // which could be valid depending on your API.
                        // If MessageResponse is guaranteed, this is an unexpected state.
                        Log.w("GeofenceRepository", "Save geofence response body is null but call was successful. Code: ${response.code()}")
                        // You might want to create a default MessageResponse or handle as error.
                        // For simplicity, treating as failure if body is expected.
                        onResult(Result.failure(Exception("Response body was null, but request successful (code ${response.code()}).")))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("GeofenceRepository", "Save geofence failed: ${response.code()} - $errorBody")
                    // Consider parsing errorBody if it's structured JSON (e.g., into an ErrorResponse class)
                    onResult(Result.failure(Exception("Failed to save geofence: ${response.code()} - $errorBody")))
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.e("GeofenceRepository", "Save geofence network call failed", t)
                onResult(Result.failure(t))
            }
        })
    }
}