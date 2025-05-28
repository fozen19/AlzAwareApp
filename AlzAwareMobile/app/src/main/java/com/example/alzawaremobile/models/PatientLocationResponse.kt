package com.example.alzawaremobile.models

data class PatientLocationResponse(
    val patientId: Long,
    val latitude: Double,
    val longitude: Double,
    val firstName: String,
    val lastName: String,
    val timestamp: String
)
