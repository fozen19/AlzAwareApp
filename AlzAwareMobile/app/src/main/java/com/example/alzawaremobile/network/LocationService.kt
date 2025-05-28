package com.example.alzawaremobile.network

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.example.alzawaremobile.models.LocationDto
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationService(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): Flow<LocationDto> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMaxUpdates(1)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    trySend(LocationDto(location.latitude, location.longitude))
                    close()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose { fusedLocationClient.removeLocationUpdates(callback) }
    }
}


