package com.example.alzawaremobile.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.alzawaremobile.R // THIS IS THE CORRECT R IMPORT
import com.example.alzawaremobile.network.ApiClient
import com.example.alzawaremobile.network.CaregiverPatientService

class CaregiverHomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var settingsButton: ImageButton
    private lateinit var addPatientButton: Button
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caregiver_home)

        // Initialize UI elements
        settingsButton = findViewById(R.id.settingsButton)
        addPatientButton = findViewById(R.id.addPatientButton)

        // Set click listener for the gear button
        settingsButton.setOnClickListener {
            toggleAddPatientButtonVisibility()
        }
        addPatientButton.setOnClickListener {
            showAddPatientDialog()
        }


        // Get the SupportMapFragment and request the map.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun toggleAddPatientButtonVisibility() {
        if (addPatientButton.visibility == View.VISIBLE) {
            addPatientButton.visibility = View.GONE
        } else {
            addPatientButton.visibility = View.VISIBLE
        }
    }

    private fun showAddPatientDialog() {
        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Enter Patient ID")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val patientId = input.text.toString().toLongOrNull()
                if (patientId != null) {
                    assignPatientToCaregiver(patientId)
                } else {
                    android.widget.Toast.makeText(this, "Invalid Patient ID", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun assignPatientToCaregiver(patientId: Long) {
        val caregiverId = getCurrentUserId() // Your logic to get logged-in caregiver ID

        val service = ApiClient.retrofit.create(CaregiverPatientService::class.java)
        val call = service.assignPatientToCaregiver(caregiverId, patientId)

        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    android.widget.Toast.makeText(this@CaregiverHomeActivity, "Patient assigned successfully!", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    android.widget.Toast.makeText(this@CaregiverHomeActivity, "Failed to assign patient", android.widget.Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                android.widget.Toast.makeText(this@CaregiverHomeActivity, "Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun getCurrentUserId(): Long {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        return sharedPreferences.getLong("userId", -1L)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}