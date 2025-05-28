package com.example.alzawaremobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alzawaremobile.models.LocationDto
import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.models.PatientLocationResponse
import com.example.alzawaremobile.network.LocationService
import com.example.alzawaremobile.repository.PatientLocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PatientLocationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PatientLocationRepository()
    private val locationService = LocationService(application.applicationContext)

    private val _locationResult = MutableStateFlow<LocationDto?>(null)
    val locationResult: StateFlow<LocationDto?> = _locationResult

    private val _updateResult = MutableStateFlow<MessageResponse?>(null)
    val updateResult: StateFlow<MessageResponse?> = _updateResult

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            locationService.getCurrentLocation().collect { location ->
                _locationResult.value = location
            }
        }
    }

    fun updatePatientLocation(
        patientId: Long,
        locationDto: LocationDto,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        repository.updatePatientLocation(patientId, locationDto) { result ->
            result.onSuccess {
                _updateResult.value = it
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Unknown error")
            }
        }
    }

    fun fetchLatestPatientLocation(patientId: Long, onResult: (PatientLocationResponse?) -> Unit) {
        repository.getLatestPatientLocation(patientId) { result ->
            result.onSuccess {
                onResult(it)
            }.onFailure {
                onResult(null)
            }
        }
    }

}
