package com.example.alzawaremobile.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy                        // ADDED: Unique work policy for WorkManager
import androidx.work.OneTimeWorkRequestBuilder               // ADDED: To build the one-time work request
import androidx.work.WorkManager                            // ADDED: To enqueue the Worker
import com.example.alzawaremobile.workers.MedicationScheduleWorker  // ADDED: Worker that handles scheduling

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val workRequest = OneTimeWorkRequestBuilder<MedicationScheduleWorker>()
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "medScheduleWork",
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }
    }
}
