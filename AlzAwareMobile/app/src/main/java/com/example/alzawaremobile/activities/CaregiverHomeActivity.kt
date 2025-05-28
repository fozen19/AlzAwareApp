package com.example.alzawaremobile.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
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
import com.example.alzawaremobile.viewmodels.PatientLocationViewModel
import com.example.alzawaremobile.viewmodels.SafeLocationViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class CaregiverHomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private val caregiverPatientViewModel: CaregiverPatientViewModel by viewModels()
    private val geofenceViewModel: GeofenceViewModel by viewModels()
    private val safeLocationViewModel: SafeLocationViewModel by viewModels()
    private val patientLocationViewModel: PatientLocationViewModel by viewModels()

    private lateinit var settingsButton: ImageButton
    private lateinit var addPatientButton: Button
    private lateinit var logoutButton: Button
    private lateinit var viewPatientsButton: Button
    private lateinit var map: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private var customSelectionMarker: Marker? = null

    private val PERMISSION_REQUEST_CODE = 1001
    private var caregiverId: Long = -1L
    private var patients: List<User> = emptyList()
    private var geofenceLocation: LatLng? = null
    private var geofenceCircle: Circle? = null
    private val safeLocationMarkers = mutableListOf<Marker>()
    private var existingGeofenceCircle: Circle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caregiver_home)

        caregiverId = TokenManager.getUserId(this)

        settingsButton = findViewById(R.id.settingsButton)
        addPatientButton = findViewById(R.id.addPatientButton)
        logoutButton = findViewById(R.id.logoutButton)
        viewPatientsButton = findViewById(R.id.viewPatientsButton)
        val etSearchAddress = findViewById<EditText>(R.id.etSearchAddress)
        val btnSearchAddress = findViewById<Button>(R.id.btnSearchAddress)

        btnSearchAddress.setOnClickListener {
            val query = etSearchAddress.text.toString()
            if (query.isBlank()) {
                Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show()
            } else {
                searchAddress(query)
            }
        }

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
        val btnSaveGeofence = findViewById<Button>(R.id.btnSaveGeofence)
        val etRadius = findViewById<EditText>(R.id.etRadius)

        btnSetGeofence.setOnClickListener {
            val radius = etRadius.text.toString().toFloatOrNull()
            if (geofenceLocation == null) {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show()
            } else if (radius == null || radius <= 0) {
                Toast.makeText(this, "Please enter a valid radius", Toast.LENGTH_SHORT).show()
            } else {
                updateGeofenceCircle(geofenceLocation!!, radius)
            }
        }

        btnSaveGeofence.setOnClickListener {
            val radius = etRadius.text.toString().toFloatOrNull()
            if (geofenceLocation == null || radius == null || radius <= 0) {
                Toast.makeText(this, "Please select a location and enter a valid radius", Toast.LENGTH_SHORT).show()
            } else {
                setGeofence(geofenceLocation!!, radius)
                saveGeofenceToBackend(geofenceLocation!!, radius.toDouble())
            }
        }

        findViewById<ImageButton>(R.id.btnZoomIn).setOnClickListener {
            map.animateCamera(CameraUpdateFactory.zoomIn())
        }
        findViewById<ImageButton>(R.id.btnZoomOut).setOnClickListener {
            map.animateCamera(CameraUpdateFactory.zoomOut())
        }
    }

    private fun searchAddress(query: String) {
        val geocoder = Geocoder(this)
        try {
            val results = geocoder.getFromLocationName(query, 1)
            if (results.isNullOrEmpty()) {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
                return
            }

            val result = results[0]
            val latLng = LatLng(result.latitude, result.longitude)
            geofenceLocation = latLng
            customSelectionMarker?.remove()
            customSelectionMarker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Search Result")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            customSelectionMarker?.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
        val input = EditText(this)
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
                setupPatientDropdown() // 🔄 dropdown'u ve hastaya ait bilgileri güncelle
            },
            onError = { error ->
                Toast.makeText(this, "Failed: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isScrollGesturesEnabled = true
        map.uiSettings.isRotateGesturesEnabled = true
        map.uiSettings.isTiltGesturesEnabled = true

        map.setOnMapClickListener { latLng ->
            geofenceLocation = latLng
            customSelectionMarker?.remove()
            customSelectionMarker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Selected Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            customSelectionMarker?.showInfoWindow()
            geofenceCircle?.remove()
        }

        map.setOnMarkerClickListener { marker ->
            when (marker.tag) {
                "latest_patient_location" -> {
                    geofenceLocation = marker.position
                    Toast.makeText(this, "Son konum seçildi: ${marker.title}", Toast.LENGTH_SHORT).show()
                    marker.showInfoWindow()
                }
                else -> {
                    val location = marker.tag
                    if (location != null) {
                        geofenceLocation = marker.position
                        Toast.makeText(this, "Konum seçildi: ${marker.title}", Toast.LENGTH_SHORT).show()
                        marker.showInfoWindow()
                    }
                }
            }
            true
        }
    }


    private fun updateGeofenceCircle(center: LatLng, radius: Float) {
        geofenceCircle?.remove()
        geofenceCircle = map.addCircle(
            CircleOptions()
                .center(center)
                .radius(radius.toDouble())
                .strokeColor(0xFFFF0000.toInt())
                .fillColor(0x44FF0000.toInt())
                .strokeWidth(4f)
        )

        val zoomLevel = when {
            radius < 100 -> 18f
            radius < 200 -> 17f
            radius < 500 -> 16f
            radius < 1000 -> 15f
            else -> 14f
        }

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel))
    }

    @SuppressLint("MissingPermission")
    private fun setGeofence(location: LatLng, radius: Float) {
        if (!hasLocationPermissions()) {
            Toast.makeText(this, "Location permissions are not granted", Toast.LENGTH_SHORT).show()
            return
        }
        updateGeofenceCircle(location, radius)

        val geofence = Geofence.Builder()
            .setRequestId("GEOFENCE_ID")
            .setCircularRegion(location.latitude, location.longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
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
        } else PackageManager.PERMISSION_GRANTED
        return fineLocation == PackageManager.PERMISSION_GRANTED && backgroundLocation == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (shouldShowRationale) {
            AlertDialog.Builder(this)
                .setTitle("Location Permission Required")
                .setMessage("The app requires location permissions to enable geofence and patient tracking features.")
                .setPositiveButton("Allow") { _, _ ->
                    ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
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
                Toast.makeText(this, "Permissions granted successfully ✅", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions were not granted ❌", Toast.LENGTH_LONG).show()

                AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("To use geofence features, location permissions must be granted. Would you like to go to settings?")
                    .setPositiveButton("Settings") { _, _ ->
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = android.net.Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    private fun getSelectedPatientId(): Long {
        val spinner = findViewById<Spinner>(R.id.spinnerPatients)
        val selectedIndex = spinner.selectedItemPosition
        return if (selectedIndex in patients.indices) {
            patients[selectedIndex].id
        } else {
            -1L
        }
    }

    private fun saveGeofenceToBackend(location: LatLng, radius: Double) {
        val patientId = getSelectedPatientId()

        if (patientId <= 0) {
            Toast.makeText(this, "Invalid patient ID. Please select a valid patient.", Toast.LENGTH_SHORT).show()
            return
        }

        if (location.latitude.isNaN() || location.longitude.isNaN()) {
            Toast.makeText(this, "Invalid geofence location coordinates.", Toast.LENGTH_SHORT).show()
            return
        }

        if (radius <= 0) {
            Toast.makeText(this, "Invalid radius. Please enter a positive number.", Toast.LENGTH_SHORT).show()
            return
        }

        val geofenceRequest = GeofenceRequest(
            patientId = patientId,
            latitude = location.latitude,
            longitude = location.longitude,
            radius = radius,
            name = "Geofence for Patient $patientId"
        )

        Log.d("Geofence", "Request: $geofenceRequest")

        // ✅ BAŞARI ve HATA mesajları ekleniyor:
        geofenceViewModel.saveGeofenceDetails(
            geofenceRequest.latitude,
            geofenceRequest.longitude,
            geofenceRequest.radius,
            geofenceRequest.name,
            geofenceRequest.patientId,
            onSuccess = {
                Toast.makeText(this, "Geofence saved to backend successfully ✅", Toast.LENGTH_SHORT).show()
                showExistingGeofence(patientId)
                        },
            onError = {
                Toast.makeText(this, "Failed to save geofence ❌: $it", Toast.LENGTH_LONG).show()
            }
        )
    }


    private fun setupPatientDropdown() {
        val spinner = findViewById<Spinner>(R.id.spinnerPatients)
        caregiverPatientViewModel.getPatientsByCaregiver(
            caregiverId,
            onSuccess = { fetchedPatients ->
                patients = fetchedPatients
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    patients.map { it.firstName })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        val selectedPatientId = patients[position].id
                        loadSafeLocations(selectedPatientId)
                        loadLatestPatientLocation(selectedPatientId) // 👈 EKLENDİ
                        showExistingGeofence(selectedPatientId) // 👈 burada çağır
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            },
            onError = {
                Toast.makeText(this, "Failed to load patient list", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun loadSafeLocations(patientId: Long) {
        safeLocationViewModel.getSafeLocationsByPatient(
            patientId,
            onSuccess = { locations ->
                safeLocationMarkers.forEach { it.remove() }
                safeLocationMarkers.clear()

                locations.forEach { loc ->
                    val latLng = LatLng(loc.latitude, loc.longitude)
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(loc.locationName)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    )
                    marker?.tag = loc
                    marker?.let { safeLocationMarkers.add(it) }
                }
            },
            onError = {
                Toast.makeText(this, "Failed to load locations", Toast.LENGTH_SHORT).show()
            }
        )
    }
    private fun loadLatestPatientLocation(patientId: Long) {
        patientLocationViewModel.fetchLatestPatientLocation(
            patientId = patientId,
            onResult = { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)

                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title("Son Konum (${location.firstName} ${location.lastName})")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    )
                    marker?.tag = "latest_patient_location" // ✅ Yeşil marker'a özel tag

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                } else {
                    Toast.makeText(this, "Son konum alınamadı", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun showExistingGeofence(patientId: Long) {
        geofenceViewModel.getGeofenceByPatient(
            patientId,
            onSuccess = { geofence ->
                val center = LatLng(geofence.latitude, geofence.longitude)
                val radius = geofence.radius.toFloat()

                existingGeofenceCircle?.remove() // önceki çemberi kaldır
                existingGeofenceCircle = map.addCircle(
                    CircleOptions()
                        .center(center)
                        .radius(radius.toDouble())
                        .strokeColor(0xFF2196F3.toInt()) // mavi kenar
                        .fillColor(0x442196F3.toInt())   // yarı saydam mavi iç
                        .strokeWidth(5f)
                )
            },
            onError = {
                Toast.makeText(this, "Kayıtlı geofence bulunamadı", Toast.LENGTH_SHORT).show()
            }
        )
    }


}
