package com.example.alzawaremobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alzawaremobile.network.LocationService
import com.example.alzawaremobile.network.LocationResult
import com.example.alzawaremobile.models.CaregiverPatientMatchRequest
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.repository.CaregiverPatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CaregiverPatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CaregiverPatientRepository()
    private val locationService: LocationService = LocationService(application.applicationContext)

    private val _currentLocation = MutableStateFlow<LocationResult?>(null)
    val currentLocation: StateFlow<LocationResult?> = _currentLocation

    private val _patientLocation = MutableStateFlow<LocationResult?>(null)
    val patientLocation: StateFlow<LocationResult?> = _patientLocation

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            locationService.getCurrentLocation().collect { location ->
                _currentLocation.value = location
            }
        }
    }

    fun updateCaregiverLocation(caregiverId: Long, latitude: Double, longitude: Double) {
        repository.updateLocation(caregiverId, latitude, longitude) { result ->
            result.onSuccess {
                // Location updated successfully
            }.onFailure {
                // Handle error if needed
            }
        }
    }

    fun getCaregiverLocation(caregiverId: Long) {
        repository.getLocation(caregiverId) { result ->
            result.onSuccess { location ->
                _currentLocation.value = LocationResult(location.latitude, location.longitude)
            }
        }
    }

    fun assignPatientToCaregiver(
        caregiverId: Long,
        patientId: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val request = CaregiverPatientMatchRequest(caregiverId, patientId)

        repository.assignPatientToCaregiver(request) { result ->
            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                onError(e.message ?: "Eşleştirme başarısız oldu")
            }
        }
    }

    fun getPatientsByCaregiver(
        caregiverId: Long,
        onSuccess: (List<User>) -> Unit,
        onError: (String) -> Unit
    ) {
        repository.getPatientsByCaregiver(caregiverId) { result ->
            result.onSuccess { onSuccess(it) }
                .onFailure { onError(it.message ?: "Bir hata oluştu") }
        }
    }
}
