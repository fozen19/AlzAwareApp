package com.example.alzawaremobile.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alzawaremobile.R
import com.example.alzawaremobile.models.GeofenceRequest
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.utils.TokenManager
import com.example.alzawaremobile.viewmodels.CaregiverPatientViewModel
import com.example.alzawaremobile.viewmodels.GeofenceViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.getValue

class CaregiverHomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private val caregiverPatientViewModel: CaregiverPatientViewModel by viewModels()
    private val GeofenceViewModel: GeofenceViewModel by viewModels()
    private lateinit var settingsButton: ImageButton
    private lateinit var addPatientButton: Button
    private lateinit var logoutButton: Button
    private lateinit var viewPatientsButton: Button
    private lateinit var map: GoogleMap
    private var caregiverId: Long = -1L
    private lateinit var geofencingClient: GeofencingClient
    private var geofenceCircle: Circle? = null
    private var geofenceLocation: LatLng? = null
    private val PERMISSION_REQUEST_CODE = 1001
    private var patients: List<User> = emptyList()


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
            toggleLogoutButtonVisibility()
            toggleViewPatientsButtonVisibility()
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

        if (!hasLocationPermissions()) {
            requestLocationPermissions()
        }
        setupPatientDropdown()

        geofencingClient = LocationServices.getGeofencingClient(this)
        val btnSetGeofence = findViewById<Button>(R.id.btnSetGeofence)
        val etRadius = findViewById<EditText>(R.id.etRadius)

        btnSetGeofence.setOnClickListener {
            val radius = etRadius.text.toString().toFloatOrNull()
            if (geofenceLocation == null) {
                Toast.makeText(this, "Please select a location by tapping the map", Toast.LENGTH_SHORT).show()
            } else if (radius == null || radius <= 0) {
                Toast.makeText(this, "Enter a valid radius", Toast.LENGTH_SHORT).show()
            } else {
                setGeofence(geofenceLocation!!, radius)
                saveGeofenceToBackend(geofenceLocation!!, radius.toDouble())
            }
        }
    }

    private fun toggleAddPatientButtonVisibility() {
        addPatientButton.visibility = if (addPatientButton.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }
    private fun toggleLogoutButtonVisibility() {
        logoutButton.visibility = if (logoutButton.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }
    private fun toggleViewPatientsButtonVisibility() {
        viewPatientsButton.visibility = if (viewPatientsButton.visibility == View.VISIBLE) View.GONE else View.VISIBLE
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

        // Get caregiver's location from the backend
        caregiverPatientViewModel.getCaregiverLocation(caregiverId)

        // Observe caregiver's location
        lifecycleScope.launch {
            caregiverPatientViewModel.currentLocation.collectLatest { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    map.addMarker(MarkerOptions().position(userLatLng).title("Your Location"))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                }
            }
        }

        // Get patients and show first patient's location
        caregiverPatientViewModel.getPatientsByCaregiver(
            caregiverId,
            onSuccess = { patients ->
                if (patients.isNotEmpty()) {
                    // Get location of first patient
                    val firstPatient = patients.first()
                    caregiverPatientViewModel.getCaregiverLocation(firstPatient.id)
                }
            },
            onError = { error ->
                Toast.makeText(this, "Failed to get patients: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Listen for map clicks to set geofence location
        map.setOnMapClickListener { latLng ->
            geofenceLocation = latLng
            updateGeofenceCircle(latLng, 0f) // Temporary circle with no radius
            map.addMarker(MarkerOptions().position(latLng).title("Selected Geofence Center"))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }
    private fun updateGeofenceCircle(center: LatLng, radius: Float) {
        // Remove any existing circle
        geofenceCircle?.remove()

        // Draw a new circle
        geofenceCircle = map.addCircle(
            CircleOptions()
                .center(center)
                .radius(radius.toDouble())
                .strokeColor(0xFFFF0000.toInt()) // Red outline
                .fillColor(0x44FF0000.toInt())  // Translucent red fill
                .strokeWidth(4f)
        )
    }

    @SuppressLint("MissingPermission")
    private fun setGeofence(location: LatLng, radius: Float) {
        // Update the circle with the actual radius
        if (!hasLocationPermissions()) {
            Toast.makeText(this, "Location permissions are not granted", Toast.LENGTH_SHORT).show()
            return
        }
        updateGeofenceCircle(location, radius)

        val geofence = Geofence.Builder()
            .setRequestId("GEOFENCE_ID") // Unique ID for the geofence
            .setCircularRegion(location.latitude, location.longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE) // Geofence will not expire
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val geofencePendingIntent = GeofenceHelper.getPendingIntent(this)

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
    }

    private fun hasLocationPermissions(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val backgroundLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            PackageManager.PERMISSION_GRANTED
        }
        return fineLocation == PackageManager.PERMISSION_GRANTED && backgroundLocation == PackageManager.PERMISSION_GRANTED
    }
    private fun requestLocationPermissions() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
    }

    // Handle the user's response to the permissions dialog
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
                // Permissions granted; initialize geofencing features if needed
            } else {
                Toast.makeText(
                    this,
                    "Permissions denied. Geofencing may not work.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getSelectedPatientId(): Long {
        val spinner = findViewById<Spinner>(R.id.spinnerPatients)
        val selectedIndex = spinner.selectedItemPosition
        return if (selectedIndex in patients.indices) {
            patients[selectedIndex].id // Get the patient ID from the cached list
        } else {
            -1L // Invalid ID if the selection is out of bounds
        }
    }

    private fun saveGeofenceToBackend(location: LatLng, radius: Double) {
        val patientId = getSelectedPatientId()

        // Validate patient ID
        if (patientId <= 0) {
            Toast.makeText(this, "Invalid patient ID. Please select a valid patient.", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate location coordinates
        if (location.latitude.isNaN() || location.longitude.isNaN()) {
            Toast.makeText(this, "Invalid geofence location coordinates.", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate radius
        if (radius <= 0) {
            Toast.makeText(this, "Invalid radius. Please enter a positive number.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the request
        val geofenceRequest = GeofenceRequest(
            patientId = patientId,
            latitude = location.latitude,
            longitude = location.longitude,
            radius = radius,
            name = "Geofence for Patient $patientId",
        )

        // Log the request for debugging
        Log.d("Geofence", "Request: $geofenceRequest")

        // Save the geofence
        GeofenceViewModel.saveGeofenceDetails(geofenceRequest.latitude, geofenceRequest.longitude, geofenceRequest.radius, geofenceRequest.name, geofenceRequest.patientId)
    }
    private fun setupPatientDropdown() {
        val spinner = findViewById<Spinner>(R.id.spinnerPatients)

        // Get caregiverId from TokenManager
        val caregiverId = TokenManager.getUserId(this)
        if (caregiverId == -1L) {
            Toast.makeText(this, "Caregiver ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        caregiverPatientViewModel.getPatientsByCaregiver(caregiverId, onSuccess = { fetchedPatients ->
            if (fetchedPatients.isNotEmpty()) {
                this.patients = fetchedPatients // Cache the fetched patients list locally

                // Populate spinner with patient names
                val patientNames = this.patients.map { it.firstName } // Assuming `User` has a name field
                val adapter = ArrayAdapter(
                    this@CaregiverHomeActivity,
                    android.R.layout.simple_spinner_item,
                    patientNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            } else {
                Toast.makeText(this, "No patients found for this caregiver.", Toast.LENGTH_SHORT).show()
            }
        }, onError = { error ->
            Toast.makeText(this, "Failed to load patients: $error", Toast.LENGTH_SHORT).show()
        })

    }

}
