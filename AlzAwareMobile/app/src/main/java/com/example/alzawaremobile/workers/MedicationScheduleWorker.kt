package com.example.alzawaremobile.workers

import android.content.Context
import android.util.Log                                         // ADDED: for logging
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.alzawaremobile.repository.CaregiverPatientRepository
import com.example.alzawaremobile.repository.MedicineRepository
import com.example.alzawaremobile.repository.getPatientsByCaregiverSuspend
import com.example.alzawaremobile.repository.getMedicinesByPatientSuspend
import com.example.alzawaremobile.utils.MedicationReminderScheduler
import com.example.alzawaremobile.utils.TokenManager

class MedicationScheduleWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val caregiverRepo = CaregiverPatientRepository()
    private val medicineRepo  = MedicineRepository()

    override suspend fun doWork(): Result {
        val ctx    = applicationContext
        val userId = TokenManager.getUserId(ctx)
        val role   = TokenManager.getUserRole(ctx) ?: return Result.failure()

        Log.d("MedicationScheduleWorker", "doWork started for userId=$userId, role=$role")  // ADDED

        return try {
            if (role == "CAREGIVER") {
                val patients = caregiverRepo.getPatientsByCaregiverSuspend(userId)
                Log.d("MedicationScheduleWorker", "Fetched ${patients.size} patients for caregiver $userId")  // ADDED

                patients.forEach { patient ->
                    val patientName = "${patient.firstName} ${patient.lastName}"
                    val meds = medicineRepo.getMedicinesByPatientSuspend(patient.id)
                    Log.d("MedicationScheduleWorker", "Scheduling ${meds.size} meds for patient $patientName")  // ADDED

                    if (meds.isNotEmpty()) {
                        MedicationReminderScheduler.scheduleAll(ctx, meds, patientName)
                    }
                }
            } else {
                val meds = medicineRepo.getMedicinesByPatientSuspend(userId)
                Log.d("MedicationScheduleWorker", "Scheduling ${meds.size} meds for self userId=$userId")  // ADDED

                if (meds.isNotEmpty()) {
                    MedicationReminderScheduler.scheduleAll(ctx, meds, "")
                }
            }

            Log.d("MedicationScheduleWorker", "doWork completed successfully")  // ADDED
            Result.success()
        } catch (e: Exception) {
            Log.e("MedicationScheduleWorker", "Error in doWork", e)  // ADDED
            Result.failure()
        }
    }
}
