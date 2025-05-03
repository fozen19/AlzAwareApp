package com.example.alzawaremobile.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.semantics.text
import androidx.glance.visibility
import android.content.Intent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.alzawaremobile.R
import com.example.alzawaremobile.models.CaregiverPatientMatchRequest
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.ApiService
import com.example.alzawaremobile.network.CaregiverPatientService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CaregiverHomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var settingsButton: ImageButton
    private lateinit var addPatientButton: Button
    private lateinit var logoutButton: Button
    private lateinit var map: GoogleMap
    private var caregiverId: Long = -1L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caregiver_home)

        // Initialize UI elements
        caregiverId = intent.getLongExtra("caregiverId", -1L)

        settingsButton = findViewById(R.id.settingsButton)
        addPatientButton = findViewById(R.id.addPatientButton)
        logoutButton = findViewById(R.id.logoutButton)

        // Set click listeners
        settingsButton.setOnClickListener {
            toggleAddPatientButtonVisibility()
        }
        addPatientButton.setOnClickListener {
            showAddPatientDialog()
        }

        // Logout: go back to RoleSelectionActivity
        logoutButton.setOnClickListener {
            val intent = Intent(this, RoleSelectionActivity::class.java)
            // Clear back stack so user can't navigate back
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        // Get the SupportMapFragment and request the map.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun toggleAddPatientButtonVisibility() {
        addPatientButton.visibility = if (addPatientButton.visibility == View.VISIBLE) {
            View.GONE
        } else {
            View.VISIBLE
        }
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
                    assignPatientToCaregiver(patientId)
                } else {
                    Toast.makeText(this, "Invalid Patient ID", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun assignPatientToCaregiver(patientId: Long) {
        if (caregiverId < 0) {
            Toast.makeText(this, "Error: caregiverId not provided", Toast.LENGTH_LONG).show()
            return
        }
        val service = ApiClient.retrofit.create(ApiService::class.java)
        val body = CaregiverPatientMatchRequest(caregiverId, patientId)
        val call = service.assignPatientToCaregiver(body)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CaregiverHomeActivity, "Patient assigned successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "Patient or caregiver not found."
                        409 -> "Patient already assigned to this caregiver."
                        else -> "Failed to assign patient. Code: ${response.code()}"
                    }
                    Toast.makeText(this@CaregiverHomeActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CaregiverHomeActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCurrentUserId(): Long {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        return sharedPreferences.getLong("userId", -1L)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in Sydney and move the camera (for now)
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}