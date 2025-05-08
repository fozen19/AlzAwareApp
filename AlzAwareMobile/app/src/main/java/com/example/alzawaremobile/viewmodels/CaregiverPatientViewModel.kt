package com.example.alzawaremobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.alzawaremobile.models.CaregiverPatientMatchRequest
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.repository.CaregiverPatientRepository

class CaregiverPatientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CaregiverPatientRepository()

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
