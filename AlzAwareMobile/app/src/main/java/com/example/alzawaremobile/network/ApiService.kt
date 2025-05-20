package com.example.alzawaremobile.network

import com.example.alzawaremobile.models.AuthResponse
import com.example.alzawaremobile.models.CaregiverPatientMatchRequest
import com.example.alzawaremobile.models.GeofenceRequest
import com.example.alzawaremobile.models.LoginRequest
import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.models.SignupRequest
import com.example.alzawaremobile.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("api/auth/signup")
    fun signup(@Body request: SignupRequest): Call<MessageResponse>

    @POST("api/caregiver-patient/assign")
    fun assignPatientToCaregiver(
        @Body request: CaregiverPatientMatchRequest
    ): Call<MessageResponse>

    @GET("api/caregiver-patient/patients/{caregiverId}")
    fun getPatientsByCaregiver(@Path("caregiverId") caregiverId: Long): Call<List<User>>

    @POST("api/geofence/save")
    fun saveGeofence(@Body request: GeofenceRequest): Call<MessageResponse>

}
