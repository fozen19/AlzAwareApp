package com.example.alzawaremobile.activities

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.alzawaremobile.R
import com.example.alzawaremobile.receivers.MedicationTakenReceiver
import com.example.alzawaremobile.utils.AlarmScheduler
import com.example.alzawaremobile.utils.NotificationHelper
import com.example.alzawaremobile.utils.TokenManager

class ScheduledNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val extras = intent.extras ?: return
        val requestCode = extras.getInt(AlarmScheduler.EXTRA_REQUEST_CODE, -1)
        val medName     = extras.getString(AlarmScheduler.EXTRA_MEDICINE_NAME, "İlaç")
        val medUsage    = extras.getString(AlarmScheduler.EXTRA_MEDICINE_USAGE, "")
        val patientName = extras.getString(AlarmScheduler.EXTRA_PATIENT_NAME)
        val role        = TokenManager.getUserRole(context) ?: "patient"

        val usage = if (medUsage == "0" ) {
            "on an empty stomach"
        } else {
            "on an full stomach"
        }

        val (title, content) = if (role == "CAREGIVER" && !patientName.isNullOrBlank()) {
            "Medication Time" to
                    "Your patient $patientName should take $medName $usage."
        } else {
            // (your patient‐side text here, e.g.)
            "Medication Time" to
                    "Please take your $medName $usage."
        }

        val takenIntent = Intent(context, MedicationTakenReceiver::class.java).apply {
            action = "ACTION_MEDICATION_TAKEN"
            putExtra(AlarmScheduler.EXTRA_REQUEST_CODE, requestCode)
        }
        val takenPending = PendingIntent.getBroadcast(
            context,
            requestCode,
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val takenAction = NotificationCompat.Action.Builder(
            R.drawable.home_24px, "Taken", takenPending
        ).build()

        val actions = listOf(takenAction)

        val details = NotificationHelper.NotificationDetails(
            context        = context,
            channelId      = NotificationHelper.MEDICATION_CHANNEL_ID,
            notificationId = requestCode,
            title          = title,
            contentText    = content,
            pendingIntent  = null,
            actions        = actions
        )
        NotificationHelper.showNotification(details)
        Log.d("ScheduledReceiver", "Notification shown: role=$role, med=$medName, patient=$patientName")
    }
}
