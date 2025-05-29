package com.example.alzawaremobile.repository

import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.models.Medicine
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun CaregiverPatientRepository.getPatientsByCaregiverSuspend(
    caregiverId: Long
): List<User> = suspendCancellableCoroutine { cont ->
    getPatientsByCaregiver(caregiverId) { result ->
        result.fold(
            onSuccess = { users   -> cont.resume(users) },
            onFailure = { error   -> cont.resumeWithException(error) }
        )
    }
}

suspend fun MedicineRepository.getMedicinesByPatientSuspend(
    patientId: Long
): List<Medicine> = suspendCancellableCoroutine { cont ->
    getMedicinesByPatient(patientId) { result ->
        result.fold(
            onSuccess = { meds    -> cont.resume(meds) },
            onFailure = { error   -> cont.resumeWithException(error) }
        )
    }
}
