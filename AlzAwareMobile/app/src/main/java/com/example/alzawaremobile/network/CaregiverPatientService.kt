package com.example.alzawaremobile.network

import com.example.alzawaremobile.models.CaregiverPatientMatchRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface CaregiverPatientService {
    @POST("api/caregiver-patient/assign") // Replace with your actual endpoint
    fun assignPatientToCaregiver(@Body request: CaregiverPatientMatchRequest): Call<Void>

}