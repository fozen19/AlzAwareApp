package com.example.alzawaremobile.activities
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object GeofenceHelper {
    fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}