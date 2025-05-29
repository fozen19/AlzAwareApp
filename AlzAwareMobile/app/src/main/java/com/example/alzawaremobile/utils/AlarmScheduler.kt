package com.example.alzawaremobile.utils // !! REPLACE !!

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.example.alzawaremobile.activities.ScheduledNotificationReceiver // !! REPLACE with your receiver path !!

object AlarmScheduler {

    const val EXTRA_REQUEST_CODE = "extra_alarm_request_code"
    const val EXTRA_MEDICINE_ID = "extra_medicine_id"
    const val EXTRA_MEDICINE_NAME = "extra_medicine_name"
    const val EXTRA_MEDICINE_USAGE = "extra_medicine_usage"
    const val EXTRA_TIME_SLOT_IDENTIFIER = "extra_time_slot_identifier"
    const val EXTRA_PATIENT_NAME             = "extra_patient_name"

    fun scheduleAlarm(
        context: Context,
        triggerAtMillis: Long,
        requestCode: Int,
        medicineId: Long,
        medicineName: String,
        medicineUsage: String,
        timeSlotIdentifier: String,
        patientName: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ScheduledNotificationReceiver::class.java).apply {
            val bundle = Bundle().apply {
                putInt(EXTRA_REQUEST_CODE, requestCode)
                putLong(EXTRA_MEDICINE_ID, medicineId)
                putString(EXTRA_MEDICINE_NAME, medicineName)
                putString(EXTRA_MEDICINE_USAGE, medicineUsage)
                putString(EXTRA_TIME_SLOT_IDENTIFIER, timeSlotIdentifier)
                putString(EXTRA_PATIENT_NAME, patientName)
            }
            putExtras(bundle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
                Log.d("AlarmScheduler", "Exact alarm scheduled: RC=$requestCode, Time=$triggerAtMillis, MedName='$medicineName'")
            } else {
                // Exact alarms are not permitted.
                // Option 1 (Silent Fail/Log): Log and potentially inform user through other means.
                //Log.w("AlarmScheduler", "Cannot schedule exact alarm. SCHEDULE_EXACT_ALARM permission not granted on Android 12+ for RC=$requestCode. Notification will not fire.")
                // Option 2 (Fallback to Inexact - Less Precise):
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                Log.w("AlarmScheduler", "Falling back to inexact alarm for RC=$requestCode due to missing SCHEDULE_EXACT_ALARM permission.")
                // Option 3 (Guide User to Settings): This is more complex and involves directing the user
                // to the app's settings page to manually grant the permission.

                // For now, we are just logging. In a production app, you'd need a strategy here.
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
            Log.d("AlarmScheduler", "Exact alarm scheduled (pre-S): RC=$requestCode, Time=$triggerAtMillis, MedName='$medicineName'")
        }
    }

    /**
     * Cancels a previously scheduled alarm.
     *
     * @param context Context to access system services.
     * @param requestCode The unique integer that was used when scheduling the alarm.
     *                    This MUST match the requestCode used in scheduleAlarm.
     */
    fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ScheduledNotificationReceiver::class.java) // The same intent as used for scheduling

        // Recreate the same PendingIntent used for scheduling to cancel it.
        // Ensure flags are also consistent if they impact PendingIntent matching (though for cancellation,
        // FLAG_NO_CREATE is often sufficient if you only want to cancel if it exists).
        // Using FLAG_IMMUTABLE here is good practice.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE // FLAG_NO_CREATE means it won't create a new one if it doesn't exist
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel() // Also cancel the PendingIntent itself
            Log.d("AlarmScheduler", "Alarm cancelled: RC=$requestCode")
        } else {
            Log.d("AlarmScheduler", "Attempted to cancel alarm with RC=$requestCode, but no such PendingIntent was found (it might have already fired or was never set).")
        }

        // Also, explicitly cancel the notification from NotificationManager if it's showing.
        // This is good practice if an alarm is cancelled before it fires but the notification
        // for a previous instance (with the same ID) might still be visible.
        // The requestCode is used as the notificationId.
        // NotificationManagerCompat.from(context).cancel(requestCode)
        // Note: For this to work reliably, ensure notification IDs are managed carefully.
        //       Usually, if an alarm is cancelled, its corresponding notification wouldn't have fired yet.
        //       This is more for cleaning up if you re-use notification IDs in complex scenarios.
    }
}