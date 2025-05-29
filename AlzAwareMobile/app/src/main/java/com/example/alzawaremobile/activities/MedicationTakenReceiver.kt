package com.example.alzawaremobile.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.alzawaremobile.utils.AlarmScheduler

class MedicationTakenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notifId = intent.getIntExtra(AlarmScheduler.EXTRA_REQUEST_CODE, -1)
        if (notifId != -1) {
            NotificationManagerCompat.from(context).cancel(notifId)
            // TODO: update backend or local DB marking this dose taken
        }
    }
}
