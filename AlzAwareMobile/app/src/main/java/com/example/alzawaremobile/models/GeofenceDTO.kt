package com.example.alzawaremobile.models

data class GeofenceDTO(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val name: String,
    val patientId: Long
)
