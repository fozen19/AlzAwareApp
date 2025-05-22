package com.example.alzawaremobile.network

import com.example.alzawaremobile.models.AuthResponse
import com.example.alzawaremobile.models.CaregiverPatientMatchRequest
import com.example.alzawaremobile.models.GeofenceRequest
import com.example.alzawaremobile.models.LoginRequest
import com.example.alzawaremobile.models.MessageResponse
import com.example.alzawaremobile.models.SafeLocation
import com.example.alzawaremobile.models.SafeLocationRequest
import com.example.alzawaremobile.models.SignupRequest
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.models.Medicine
import com.example.alzawaremobile.models.LocationDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @POST("api/safe-location/add")
    fun addSafeLocation(@Body request: SafeLocationRequest): Call<MessageResponse>

    @GET("api/safe-location/patient/{patientId}")
    fun getSafeLocationsByPatient(@Path("patientId") patientId: Long): Call<List<SafeLocation>>

    @DELETE("api/safe-location/delete/{locationId}")
    fun deleteSafeLocation(@Path("locationId") locationId: Long): Call<MessageResponse>

    @GET("/api/medicines/getMedicines/{patientId}")
    fun getMedicinesByPatient(@Path("patientId") patientId: Long): Call<List<Medicine>>

    @POST("/api/medicines/create")
    fun createMedicine(@Body medicine: Medicine): Call<Medicine>

    @GET("/api/medicines/getAMedicine/{id}")
    fun getMedicineById(@Path("id") id: Long): Call<Medicine>

    @PUT("/api/medicines/updateMedicine/{id}")
    fun updateMedicine(@Path("id") id: Long, @Body medicine: Medicine): Call<Medicine>

    @DELETE("/api/medicines/deleteMedicine/{id}")
    fun deleteMedicine(@Path("id") id: Long): Call<Void>

    @POST("api/caregivers/{id}/location")
    fun updateLocation(
        @Path("id") caregiverId: Long,
        @Body locationDto: LocationDto
    ): Call<Void>
    @POST("api/geofence/save")
    fun saveGeofence(@Body request: GeofenceRequest): Call<MessageResponse>

    @GET("api/caregivers/{id}/location")
    fun getLocation(@Path("id") caregiverId: Long): Call<LocationDto>

    @GET("api/safe-location/check/{patientId}")
    fun checkPatientLocation(@Path("patientId") patientId: Long): Call<Boolean>
}
