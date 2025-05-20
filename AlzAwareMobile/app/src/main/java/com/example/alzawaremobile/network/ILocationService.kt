package com.example.alzawaremobile.network

import kotlinx.coroutines.flow.Flow

interface ILocationService {
    fun getCurrentLocation(): Flow<LocationResult>
}

data class LocationResult(
    val latitude: Double,
    val longitude: Double
)