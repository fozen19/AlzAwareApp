package com.example.alzawaremobile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alzawaremobile.models.Medicine
import com.example.alzawaremobile.repository.MedicineRepository

class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MedicineRepository()

    private val _medicines = MutableLiveData<List<Medicine>>()
    val medicines: LiveData<List<Medicine>> = _medicines

    private val _selectedMedicine = MutableLiveData<Medicine>()
    val selectedMedicine: LiveData<Medicine> = _selectedMedicine

    private val _operationStatus = MutableLiveData<String>()
    val operationStatus: LiveData<String> = _operationStatus

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var currentPatientId: Long = -1

    fun setPatientId(patientId: Long) {
        if (currentPatientId != patientId) {
            currentPatientId = patientId
            loadMedicines(patientId)
        }
    }

    fun refreshList() {
        if (currentPatientId != -1L) {
            loadMedicines(currentPatientId)
        }
    }

    private fun loadMedicines(patientId: Long) {
        repository.getMedicinesByPatient(patientId) { result ->
            result.onSuccess {
                _medicines.postValue(it)
            }.onFailure {
                _error.postValue(it.message ?: "Failed to load medicines")
            }
        }
    }

    fun createMedicine(medicine: Medicine) {
        repository.createMedicine(medicine) { result ->
            result.onSuccess {
                _operationStatus.postValue("Medicine created successfully")
                refreshList()
            }.onFailure {
                _error.postValue(it.message ?: "Failed to create medicine")
            }
        }
    }

    fun getMedicineById(id: Long) {
        repository.getMedicineById(id) { result ->
            result.onSuccess {
                _selectedMedicine.postValue(it)
            }.onFailure {
                _error.postValue(it.message ?: "Failed to get medicine details")
            }
        }
    }

    fun updateMedicine(id: Long, medicine: Medicine) {
        repository.updateMedicine(id, medicine) { result ->
            result.onSuccess {
                _operationStatus.postValue("Medicine updated successfully")
                refreshList()
            }.onFailure {
                _error.postValue(it.message ?: "Failed to update medicine")
            }
        }
    }

    fun deleteMedicine(id: Long) {
        repository.deleteMedicine(id) { result ->
            result.onSuccess {
                _operationStatus.postValue("Medicine deleted successfully")
                refreshList()
            }.onFailure {
                _error.postValue(it.message ?: "Failed to delete medicine")
            }
        }
    }
}
