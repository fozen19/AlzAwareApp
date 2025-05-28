package com.example.alzawaremobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.repository.CaregiverRepository

class CaregiverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CaregiverRepository()

    private val _caregiverProfile = MutableLiveData<User>()
    val caregiverProfile: LiveData<User> get() = _caregiverProfile

    private val _updateResult = MutableLiveData<MessageResponse>()
    val updateResult: LiveData<MessageResponse> get() = _updateResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchCaregiverProfile(caregiverId: Long) {
        repository.getCaregiverProfile(caregiverId) { result ->
            result.onSuccess { user ->
                _caregiverProfile.postValue(user)
            }.onFailure { exception ->
                _errorMessage.postValue("Profil alınamadı: ${exception.message}")
            }
        }
    }

    fun updateCaregiverProfile(caregiverId: Long, updatedUser: User) {
        repository.updateCaregiverProfile(caregiverId, updatedUser) { result ->
            result.onSuccess { response ->
                _updateResult.postValue(response)
                // Profili güncelle
                fetchCaregiverProfile(caregiverId)
            }.onFailure { exception ->
                _errorMessage.postValue("Güncelleme başarısız: ${exception.message}")
            }
        }
    }
}
