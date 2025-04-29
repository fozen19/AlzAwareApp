package com.example.alzawaremobile.network

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CaregiverPatientService {

    @POST("api/caregiver-patient/assign")
    @FormUrlEncoded
    fun assignPatientToCaregiver(
        @Field("caregiverId") caregiverId: Long,
        @Field("patientId") patientId: Long
    ): Call<Void>
}
