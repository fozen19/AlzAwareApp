package com.example.alzawaremobile.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alzawaremobile.models.GeofenceDTO
import com.example.alzawaremobile.models.GeofenceRequest
import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.repository.GeofenceRepository
import com.example.alzawaremobile.utils.TokenManager // Your utility for getting the saved token

class GeofenceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GeofenceRepository()
    private val context = getApplication<Application>() // Context for TokenManager

    private val _saveGeofenceResult = MutableLiveData<Result<MessageResponse>>()
    val saveGeofenceResult: LiveData<Result<MessageResponse>> = _saveGeofenceResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun saveGeofenceDetails(
        latitude: Double,
        longitude: Double,
        radius: Double,
        name: String,
        patientId: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        _isLoading.value = true
        val token = TokenManager.getToken(context)

        if (token == null) {
            _isLoading.value = false
            onError("User not authenticated. Token is missing.")
            Log.e("GeofenceViewModel", "Auth token is null.")
            return
        }

        val geofenceRequest = GeofenceRequest(
            latitude = latitude,
            longitude = longitude,
            radius = radius,
            name = name,
            patientId = patientId
        )

        Log.d("GeofenceViewModel", "Sending geofence: $geofenceRequest")

        repository.saveGeofence(token, geofenceRequest) { result ->
            _isLoading.postValue(false)

            result.onSuccess {
                _saveGeofenceResult.postValue(Result.success(it))
                Log.i("GeofenceViewModel", "Geofence saved: ${it.message}")
                onSuccess()
            }.onFailure { exception ->
                _saveGeofenceResult.postValue(Result.failure(exception))
                Log.e("GeofenceViewModel", "Failed to save geofence: ${exception.message}", exception)
                onError(exception.localizedMessage ?: "Unknown error occurred")
            }
        }
    }
    fun getGeofenceByPatient(
        patientId: Long,
        onSuccess: (GeofenceDTO) -> Unit,
        onError: (String) -> Unit
    ) {
        repository.getGeofenceByPatient(patientId) { result ->
            result.onSuccess { onSuccess(it) }
                .onFailure { onError(it.message ?: "Geofence yüklenemedi") }
        }
    }


    // Optional: Function to clear the result, e.g., after the UI has consumed it
    fun clearSaveResult() {
        _saveGeofenceResult.value = Result.success(MessageResponse(message = "")) // Clear with an empty result
    }
}