package com.example.alzawaremobile.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent?.hasError() ?: true) {
            val errorMessage = "Geofence error: ${geofencingEvent?.errorCode}"
            Log.e("GeofenceReceiver", errorMessage)
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        // Get the geofence transition type
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Handle the geofence transition
        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                handleEvent(context, "Entered the geofence")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                handleEvent(context, "Exited the geofence")
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                handleEvent(context, "Dwelling in the geofence")
            }
            else -> {
                Log.e("GeofenceReceiver", "Unknown geofence transition")
            }
        }
    }

    private fun handleEvent(context: Context, message: String) {
        Log.i("GeofenceReceiver", message)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        // You can add custom logic here, like notifying the caregiver
    }
}