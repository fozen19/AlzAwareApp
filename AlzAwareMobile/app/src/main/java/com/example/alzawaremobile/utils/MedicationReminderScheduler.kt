package com.example.alzawaremobile.utils

import android.content.Context
import com.example.alzawaremobile.models.Medicine
import java.util.*

object MedicationReminderScheduler {

    fun scheduleAll(context: Context, medicines: List<Medicine>, patientName: String?) {
        medicines.forEach { med ->
            // For each slot, either schedule or cancel
            listOf(TimeSlot.MORNING, TimeSlot.AFTERNOON, TimeSlot.EVENING).forEach { slot ->
                val code = med.id?.let { computeRequestCode(it, slot) }
                val flag = when(slot) {
                    TimeSlot.MORNING   -> med.inMorning
                    TimeSlot.AFTERNOON -> med.inAfternoon
                    TimeSlot.EVENING   -> med.inEvening
                }
                if (flag == 1) {
                    // schedule exactly as before
                    scheduleSlot(context, med, slot, code, patientName)
                } else {
                    // cancel any existing alarm for this slot
                    if (code != null) {
                        AlarmScheduler.cancelAlarm(context, code)
                    }
                }
            }
        }
    }

    private fun computeRequestCode(medId: Long, slot: TimeSlot): Int {
        // must match your old logic exactly
        return ((medId % Int.MAX_VALUE).toInt() * 3) + slot.ordinal
    }

    private fun scheduleSlot(
        context: Context,
        med: Medicine,
        slot: TimeSlot,
        code: Int?,
        patientName: String?
    ) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, TimeSlotConfig.hourOfDay[slot]!!)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DATE, 1)
        }

        val requestCode = ((med.id!! % Int.MAX_VALUE).toInt() * 3) + slot.ordinal

        //test için kullan
        val twoMinutesFromNow = System.currentTimeMillis() + 1 * 30_000L

        AlarmScheduler.scheduleAlarm(
            context             = context,
            triggerAtMillis     = cal.timeInMillis,  //cal.timeInMillis,   twoMinutesFromNow
            requestCode         = requestCode,
            medicineId          = med.id,
            medicineName        = med.name,
            medicineUsage       = med.usage.toString(),
            timeSlotIdentifier  = slot.name,
            patientName = patientName.toString()
        )
    }
}
