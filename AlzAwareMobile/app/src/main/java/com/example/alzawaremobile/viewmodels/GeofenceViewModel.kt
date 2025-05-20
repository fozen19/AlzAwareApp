package com.example.alzawaremobile.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
        patientId: Long // Make sure you have access to the relevant patientId
    ) {
        _isLoading.value = true // Indicate loading start (for UI)
        val token = TokenManager.getToken(context) // Get the saved auth token

        if (token == null) {
            _saveGeofenceResult.value = Result.failure(Exception("User not authenticated. Token is missing."))
            _isLoading.value = false // Reset loading state
            Log.e("GeofenceViewModel", "Cannot save geofence: Auth token is null.")
            return
        }

        val geofenceRequest = GeofenceRequest(
            latitude  = latitude,
            longitude  = longitude,
            radius = radius,
            name = name,
            patientId = patientId,
        )

        Log.d("GeofenceViewModel", "Attempting to save geofence: $geofenceRequest with token: $token")

        repository.saveGeofence(token, geofenceRequest) { result ->
            // Update LiveData on the main thread if the callback is not on the main thread
            // For simplicity, using postValue which handles this.
            _isLoading.postValue(false) // Indicate loading finished
            _saveGeofenceResult.postValue(result)

            result.onSuccess {
                Log.i("GeofenceViewModel", "Geofence save successful: ${it.message}")
                // You might want to trigger navigation or show a success message here
            }.onFailure { exception ->
                Log.e("GeofenceViewModel", "Geofence save failed: ${exception.message}", exception)
                // You might want to show an error message to the user here
            }
        }
    }

    // Optional: Function to clear the result, e.g., after the UI has consumed it
    fun clearSaveResult() {
        _saveGeofenceResult.value = Result.success(MessageResponse(message = "")) // Clear with an empty result
    }
}