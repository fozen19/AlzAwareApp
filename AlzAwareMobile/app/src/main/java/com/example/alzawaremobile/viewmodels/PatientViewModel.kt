package com.example.alzawaremobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.repository.PatientRepository

class PatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PatientRepository()

    val patientProfile = MutableLiveData<User>()
    val updateResult = MutableLiveData<MessageResponse>()
    val errorMessage = MutableLiveData<String>()

    fun fetchPatientProfile(patientId: Long) {
        repository.getPatientById(patientId) { result ->
            result.onSuccess { patientProfile.postValue(it) }
                .onFailure { errorMessage.postValue("Profil alınamadı: ${it.message}") }
        }
    }

    fun updatePatientProfile(patientId: Long, updatedUser: User) {
        repository.updatePatient(patientId, updatedUser) { result ->
            result.onSuccess {
                updateResult.postValue(it)
                fetchPatientProfile(patientId)
            }.onFailure {
                errorMessage.postValue("Güncelleme başarısız: ${it.message}")
            }
        }
    }
}
