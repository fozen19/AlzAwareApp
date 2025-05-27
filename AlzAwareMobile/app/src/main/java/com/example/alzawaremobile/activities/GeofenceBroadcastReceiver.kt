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
            val errorMessage = "Geofence error occurred: ${geofencingEvent?.errorCode}"
            Log.e("GeofenceReceiver", errorMessage)
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                handleEvent(context, "You have entered the safe area.")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                handleEvent(context, "You have exited the safe area!")
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                handleEvent(context, "You are staying within the safe area.")
            }
            else -> {
                Log.e("GeofenceReceiver", "Unknown geofence transition detected.")
            }
        }
    }

    private fun handleEvent(context: Context, message: String) {
        Log.i("GeofenceReceiver", message)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        // You can add custom logic here, like sending a notification to the caregiver
    }
}
