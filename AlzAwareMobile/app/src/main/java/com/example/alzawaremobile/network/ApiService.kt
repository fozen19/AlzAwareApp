package com.example.alzawaremobile.network

import com.example.alzawaremobile.models.AuthResponse
import com.example.alzawaremobile.models.CaregiverSignupRequest
import com.example.alzawaremobile.models.LoginRequest
import com.example.alzawaremobile.models.PatientSignupRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/patients/signup")
    fun signupPatient(@Body request: PatientSignupRequest): Call<AuthResponse>

    @POST("api/caregivers/signup")
    fun signupCaregiver(@Body request: CaregiverSignupRequest): Call<AuthResponse>

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>
}
