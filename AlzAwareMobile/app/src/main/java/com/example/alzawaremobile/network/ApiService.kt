package com.example.alzawaremobile.network

import com.example.alzawaremobile.models.AuthResponse
import com.example.alzawaremobile.models.CaregiverPatientMatchRequest
import com.example.alzawaremobile.models.LoginRequest
import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.models.SignupRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("api/auth/signup")
    fun signup(@Body request: SignupRequest): Call<MessageResponse>

    @POST("api/caregiver-patient/assign") // Replace with your actual endpoint
    fun assignPatientToCaregiver(@Body request: CaregiverPatientMatchRequest): Call<Void>
}
