package com.example.alzawaremobile.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.alzawaremobile.models.LocationDto
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.ApiService
import com.example.alzawaremobile.network.LocationService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.HttpException
import java.io.IOException

class PatientLocationUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val locationService = LocationService(context)
    private val apiService = ApiClient.createService(ApiService::class.java)

    override suspend fun doWork(): Result {
        return try {
            val patientId = inputData.getLong("patient_id", -1L)
            if (patientId <= 0) return Result.failure()

            // Try to get location with timeout
            val locationDto: LocationDto? = withTimeoutOrNull(10000L) {
                locationService.getCurrentLocation().first()
            }

            if (locationDto == null) {
                return Result.retry() // location not received in time
            }

            val response = apiService.updatePatientLocation(patientId, locationDto).execute()

            if (response.isSuccessful) {
                Result.success()
            } else {
                Result.retry() // e.g. server error
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Result.retry() // network problem
        } catch (e: HttpException) {
            e.printStackTrace()
            Result.retry()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
