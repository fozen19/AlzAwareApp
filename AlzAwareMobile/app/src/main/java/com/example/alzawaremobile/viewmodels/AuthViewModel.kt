package com.example.alzawaremobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.alzawaremobile.models.LoginRequest
import com.example.alzawaremobile.models.SignupRequest
import com.example.alzawaremobile.repository.AuthRepository
import com.example.alzawaremobile.utils.TokenManager

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AuthRepository()
    private val context = getApplication<Application>()

    // Kullanıcı kayıt (signup)
    fun signup(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?,
        role: String, // caregiver veya patient
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val backendRoleString = when (role.lowercase()) { // Use lowercase for case-insensitivity
            "patient" -> "PATIENT"
            "caregiver" -> "CAREGIVER"
            "doctor" -> "DOCTOR" // Add other roles as needed
            "admin" -> "ADMIN"
            else -> {
                onError("Invalid role provided: $role")
                return // Stop execution if the role is invalid
            }
        }
        val request = SignupRequest(
            username = username,
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            role = backendRoleString // Backend Set<String> olarak role bekliyor
        )

        repo.signup(request) { result ->
            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                onError(e.message ?: "Kayıt başarısız oldu")
            }
        }
    }

    // Kullanıcı giriş (login)
    fun login(
        username: String,
        password: String,
        role: String,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = LoginRequest(
            username = username,
            password = password
        )

        repo.login(request) { result ->
            result.onSuccess { authResponse ->
                if (authResponse.token.isNotEmpty()) {
                    TokenManager.saveToken(context, authResponse.token)
                    TokenManager.saveUserRole(context, role)
                    TokenManager.saveUserId(context, authResponse.id)
                    onSuccess(authResponse.id)
                } else {
                    onError("Geçersiz token alındı")
                }
            }.onFailure { e ->
                onError(e.message ?: "Giriş başarısız oldu")
            }
        }
    }
}
