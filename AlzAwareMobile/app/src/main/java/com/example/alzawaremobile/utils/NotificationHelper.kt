package com.example.alzawaremobile.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.alzawaremobile.MainActivity
import com.example.alzawaremobile.R

object NotificationHelper {
    const val MEDICATION_CHANNEL_ID = "medication_reminder_channel_v1"

    data class NotificationDetails(
        val context: Context,
        val channelId: String,
        val notificationId: Int,
        val title: String,
        val contentText: String,
        val smallIconResId: Int = R.drawable.medication_24px,
        val pendingIntent: PendingIntent? = null,
        val autoCancel: Boolean = true,
        val category: String? = NotificationCompat.CATEGORY_REMINDER,
        val actions: List<NotificationCompat.Action> = emptyList()
    )

    fun showNotification(details: NotificationDetails) {
        val builder = NotificationCompat.Builder(details.context, details.channelId)
            .setSmallIcon(details.smallIconResId)
            .setContentTitle(details.title)
            .setContentText(details.contentText)
            .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
            .setAutoCancel(details.autoCancel)
            .setCategory(details.category) // Set category for Do Not Disturb etc.

        details.pendingIntent?.let { builder.setContentIntent(it) }
        details.actions.forEach { builder.addAction(it) }

        with(NotificationManagerCompat.from(details.context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        details.context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.w("NotificationHelper", "POST_NOTIFICATIONS permission not granted. Cannot show notification.")
                    return
                }
            }
            try {
                notify(details.notificationId, builder.build())
                Log.d("NotificationHelper", "Notification shown: ID=${details.notificationId}, Title='${details.title}'")
            } catch (e: SecurityException) {
                Log.e("NotificationHelper", "SecurityException on notify. Check POST_NOTIFICATIONS permission.", e)
            }
        }
    }

    fun createMedicationTapIntent(context: Context, medicineId: Long, notificationManagerId: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply { // Or your MedicineDetailsActivity
            putExtra("EXTRA_MEDICINE_ID", medicineId)
            putExtra("EXTRA_FROM_NOTIFICATION_ID", notificationManagerId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            notificationManagerId, // Use the notificationId as part of the requestCode for uniqueness
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}