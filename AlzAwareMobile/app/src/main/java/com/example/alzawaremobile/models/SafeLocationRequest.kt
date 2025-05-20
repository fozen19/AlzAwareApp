package com.example.alzawaremobile.models

data class SafeLocationRequest(
    val patientId: Long,
    val locationName: String,
    val latitude: Double,
    val longitude: Double
)
