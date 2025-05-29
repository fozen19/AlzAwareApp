package com.example.alzawaremobile.utils

enum class TimeSlot { MORNING, AFTERNOON, EVENING }

object TimeSlotConfig {
    val hourOfDay = mapOf(
        TimeSlot.MORNING   to 9,
        TimeSlot.AFTERNOON to 15,
        TimeSlot.EVENING   to 21,
    )
}