package com.example.alzawaremobile.network

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface CaregiverPatientService {
    @POST("caregivers/{caregiverId}/patients/{patientId}") // Replace with your actual endpoint
    fun assignPatientToCaregiver(
        @Path("caregiverId") caregiverId: Long,
        @Path("patientId") patientId: Long
    ): Call<Void>
}