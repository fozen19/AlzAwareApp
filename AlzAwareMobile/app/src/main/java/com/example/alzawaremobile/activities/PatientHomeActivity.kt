package com.example.alzawaremobile.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.alzawaremobile.databinding.ActivityPatientHomeBinding
import com.example.alzawaremobile.utils.TokenManager
import com.example.alzawaremobile.viewmodels.PatientLocationViewModel
import com.example.alzawaremobile.workers.PatientLocationUpdateWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.TimeUnit

class PatientHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientHomeBinding
    private val viewModel: PatientLocationViewModel by viewModels()
    private val PERMISSION_REQUEST_CODE = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val patientIdFromLogin = intent.getLongExtra("patientId", -1L)
        if (patientIdFromLogin > 0) {
            TokenManager.saveUserId(this, patientIdFromLogin)
        }

        binding.tvWelcome.text = "Hoş geldiniz, Hasta!"

        requestLocationPermissions()
        startLocationWorkerIfPermitted()

        binding.btnSendLocation.setOnClickListener {
            if (!hasLocationPermissions()) {
                Toast.makeText(this, "Konum izinleri verilmedi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val patientId = TokenManager.getUserId(this)
            if (patientId <= 0) {
                Toast.makeText(this, "Hasta ID'si bulunamadı", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val locationDto = withTimeoutOrNull(10000L) {
                        viewModel.fetchCurrentLocation()
                        viewModel.locationResult.first { it != null }
                    }

                    if (locationDto == null) {
                        Toast.makeText(this@PatientHomeActivity, "Konum alınamadı", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    viewModel.updatePatientLocation(
                        patientId,
                        locationDto,
                        onSuccess = {
                            Toast.makeText(this@PatientHomeActivity, "Konum gönderildi ✅", Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(this@PatientHomeActivity, "Gönderilemedi ❌: $it", Toast.LENGTH_SHORT).show()
                        }
                    )

                } catch (e: Exception) {
                    Toast.makeText(this@PatientHomeActivity, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestLocationPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        ActivityCompat.requestPermissions(
            this,
            permissions.toTypedArray(),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun hasLocationPermissions(): Boolean {
        val fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val background = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else PackageManager.PERMISSION_GRANTED

        return fine == PackageManager.PERMISSION_GRANTED &&
                coarse == PackageManager.PERMISSION_GRANTED &&
                background == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationWorkerIfPermitted() {
        if (!hasLocationPermissions()) return

        val patientId = TokenManager.getUserId(this)
        if (patientId <= 0) {
            Toast.makeText(this, "Hasta ID bulunamadı", Toast.LENGTH_SHORT).show()
            return
        }

        val workRequest = PeriodicWorkRequestBuilder<PatientLocationUpdateWorker>(15, TimeUnit.MINUTES)
            .setInputData(workDataOf("patient_id" to patientId))
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PatientLocationUpdate",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                Toast.makeText(this, "İzinler verildi ✅", Toast.LENGTH_SHORT).show()
                startLocationWorkerIfPermitted()
            } else {
                Toast.makeText(this, "Konum izinleri gerekli ❌", Toast.LENGTH_LONG).show()
            }
        }
    }
}
