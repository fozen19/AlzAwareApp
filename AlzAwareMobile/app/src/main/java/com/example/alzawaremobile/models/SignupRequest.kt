package com.example.alzawaremobile.models


data class SignupRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val role: String
)
