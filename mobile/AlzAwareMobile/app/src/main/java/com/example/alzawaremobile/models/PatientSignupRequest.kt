package com.example.alzawaremobile.models

data class PatientSignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val caregiverId: String
)
