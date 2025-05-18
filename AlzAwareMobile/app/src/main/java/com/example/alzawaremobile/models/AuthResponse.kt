package com.example.alzawaremobile.models

data class AuthResponse(
    val token: String,
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val roles: String,
)

