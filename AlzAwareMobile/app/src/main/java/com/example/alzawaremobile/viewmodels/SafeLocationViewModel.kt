package com.example.alzawaremobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.alzawaremobile.models.SafeLocation
import com.example.alzawaremobile.models.SafeLocationRequest
import com.example.alzawaremobile.repository.SafeLocationRepository

class SafeLocationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SafeLocationRepository()

    fun addSafeLocation(
        patientId: Long,
        locationName: String,
        latitude: Double,
        longitude: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val request = SafeLocationRequest(patientId, locationName, latitude, longitude)
        repository.addSafeLocation(request) { result ->
            result.onSuccess { onSuccess() }
            result.onFailure { onError(it.message ?: "Bilinmeyen hata oluştu") }
        }
    }

    fun getSafeLocationsByPatient(
        patientId: Long,
        onSuccess: (List<SafeLocation>) -> Unit,
        onError: (String) -> Unit
    ) {
        repository.getSafeLocationsByPatient(patientId) { result ->
            result.onSuccess { onSuccess(it) }
                .onFailure { onError(it.message ?: "Safe location verisi alınamadı") }
        }
    }

}
