package com.example.alzawaremobile.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.alzawaremobile.models.LocationDto
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.ApiService
import com.example.alzawaremobile.network.LocationService
import kotlinx.coroutines.flow.first

class LocationUpdateWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val apiService: ApiService = ApiClient.createService(ApiService::class.java)
    private val locationService = LocationService(context)

    override suspend fun doWork(): Result {
        try {
            // Get caregiver ID from input data
            val caregiverId = inputData.getLong("caregiver_id", -1L)
            if (caregiverId == -1L) return Result.failure()
            
            // Get current location
            val location = locationService.getCurrentLocation().first()
            
            // Update location on server
            val response = apiService.updateLocation(
                caregiverId,
                LocationDto(location.latitude, location.longitude)
            ).execute()
            
            return if (response.isSuccessful) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            return Result.retry()
        }
    }
} 