package com.example.alzawaremobile.models

data class GeofenceRequest(
    val latitude: Double,  // Updated to match backend field name
    val longitude: Double, // Updated to match backend field name
    val radius: Double,
    val name: String,
    val patientId: Long
)
