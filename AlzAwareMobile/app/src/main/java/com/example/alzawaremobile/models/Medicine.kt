package com.example.alzawaremobile.models

data class Medicine(
    val id: Long? = null,
    val name: String,
    val whichDayParts: Int,
    val usage: Int,
    val count: Int,
    val patient: User
)
