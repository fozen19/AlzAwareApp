package com.example.alzawaremobile.models

data class Medicine(
    val id: Long? = null,
    val name: String,
    val inMorning: Int,
    val inAfternoon: Int,
    val inEvening: Int,
    val usage: Int,
    val count: Int,
    val patient: User
)
