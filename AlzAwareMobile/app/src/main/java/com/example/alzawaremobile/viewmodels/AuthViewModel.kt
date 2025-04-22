package com.example.alzawaremobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.alzawaremobile.models.CaregiverSignupRequest
import com.example.alzawaremobile.models.LoginRequest
import com.example.alzawaremobile.models.PatientSignupRequest
import com.example.alzawaremobile.repository.AuthRepository
import com.example.alzawaremobile.utils.TokenManager

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = AuthRepository()
    private val context = getApplication<Application>()

    fun signupPatient(name: String, email: String, password: String, caregiverId: String,
                      onSuccess: () -> Unit, onError: (String) -> Unit) {
        val request = PatientSignupRequest(name, email, password, caregiverId)
        repo.signupPatient(request) {
            it.onSuccess { res ->
                if (res.success && res.token != null) {
                    TokenManager.saveToken(context, res.token)
                    TokenManager.saveUserRole(context, "patient") // 🌟 hasta rolünü kaydet
                    onSuccess()
                } else onError(res.message)
            }.onFailure { e -> onError(e.message ?: "Kayıt başarısız") }
        }
    }

    fun signupCaregiver(name: String, email: String, password: String,
                        onSuccess: () -> Unit, onError: (String) -> Unit) {
        val request = CaregiverSignupRequest(name, email, password)
        repo.signupCaregiver(request) {
            it.onSuccess { res ->
                if (res.success && res.token != null) {
                    TokenManager.saveToken(context, res.token)
                    TokenManager.saveUserRole(context, "caregiver") // 🌟 bakıcı rolünü kaydet
                    onSuccess()
                } else onError(res.message)
            }.onFailure { e -> onError(e.message ?: "Kayıt başarısız") }
        }
    }

    fun login(email: String, password: String, role: String,
              onSuccess: () -> Unit, onError: (String) -> Unit) {
        val request = LoginRequest(email, password)
        repo.login(request) {
            it.onSuccess { res ->
                if (res.success && res.token != null) {
                    TokenManager.saveToken(context, res.token)
                    TokenManager.saveUserRole(context, role) // 🌟 giriş yaparken rolü alıyoruz
                    onSuccess()
                } else onError(res.message)
            }.onFailure { e -> onError(e.message ?: "Giriş başarısız") }
        }
    }
}
