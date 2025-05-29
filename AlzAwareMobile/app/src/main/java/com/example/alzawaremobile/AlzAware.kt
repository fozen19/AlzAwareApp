package com.example.alzawaremobile

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.alzawaremobile.utils.NotificationHelper

class AlzAware : Application() {
    override fun onCreate() {
        super.onCreate()
        createMedicationNotificationChannel()
    }

    private fun createMedicationNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Medication Reminders"
            val descriptionText = "Channel for medication reminder notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NotificationHelper.MEDICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                // Optional: set light color, vibration pattern, etc.
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("AppClass", "Medication notification channel created.")
        }
    }
}