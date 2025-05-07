package com.example.alzawaremobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.R
import com.example.alzawaremobile.models.CaregiverPatientMatchRequest
import com.example.alzawaremobile.utils.TokenManager
import com.example.alzawaremobile.viewmodels.CaregiverPatientViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.getValue

class CaregiverHomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private val caregiverPatientViewModel: CaregiverPatientViewModel by viewModels()
    private lateinit var settingsButton: ImageButton
    private lateinit var addPatientButton: Button
    private lateinit var logoutButton: Button
    private lateinit var viewPatientsButton: Button
    private lateinit var map: GoogleMap
    private var caregiverId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caregiver_home)

        caregiverId = TokenManager.getUserId(this)

        settingsButton = findViewById(R.id.settingsButton)
        addPatientButton = findViewById(R.id.addPatientButton)
        logoutButton = findViewById(R.id.logoutButton)
        viewPatientsButton = findViewById(R.id.viewPatientsButton)

        settingsButton.setOnClickListener {
            toggleAddPatientButtonVisibility()
        }

        addPatientButton.setOnClickListener {
            showAddPatientDialog()
        }

        viewPatientsButton.setOnClickListener {
            val intent = Intent(this, ViewPatientsActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, RoleSelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun toggleAddPatientButtonVisibility() {
        addPatientButton.visibility = if (addPatientButton.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun showAddPatientDialog() {
        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter Patient ID")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val patientId = input.text.toString().toLongOrNull()
                if (patientId != null) {
                    assignPatientToCaregiver(caregiverId, patientId)
                } else {
                    Toast.makeText(this, "Invalid Patient ID", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun assignPatientToCaregiver(caregiverId: Long, patientId: Long) {
        if (caregiverId < 0) {
            Toast.makeText(this, "Error: caregiverId not provided", Toast.LENGTH_LONG).show()
            return
        }

        caregiverPatientViewModel.assignPatientToCaregiver(
            caregiverId,
            patientId,
            onSuccess = {
                Toast.makeText(this, "Patient assigned successfully!", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                Toast.makeText(this, "Failed: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
