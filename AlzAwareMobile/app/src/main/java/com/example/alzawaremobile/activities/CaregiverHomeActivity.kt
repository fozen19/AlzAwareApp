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
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.alzawaremobile.R
import com.example.alzawaremobile.models.GeofenceRequest
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.utils.TokenManager
import com.example.alzawaremobile.viewmodels.CaregiverPatientViewModel
import com.example.alzawaremobile.viewmodels.GeofenceViewModel
import com.example.alzawaremobile.viewmodels.SafeLocationViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CaregiverHomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private val caregiverPatientViewModel: CaregiverPatientViewModel by viewModels()
    private val geofenceViewModel: GeofenceViewModel by viewModels()
    private val safeLocationViewModel: SafeLocationViewModel by viewModels()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caregiver_home)

        // UI tanımları ve listenerlar...
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
                Toast.makeText(this, "Lütfen bir adres girin", Toast.LENGTH_SHORT).show()
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

        // onCreate içinde:
        val btnSetGeofence = findViewById<Button>(R.id.btnSetGeofence)
        val btnSaveGeofence = findViewById<Button>(R.id.btnSaveGeofence)
        val etRadius = findViewById<EditText>(R.id.etRadius)

    // Sadece daireyi çizer
        btnSetGeofence.setOnClickListener {
            val radius = etRadius.text.toString().toFloatOrNull()
            if (geofenceLocation == null) {
                Toast.makeText(this, "Lütfen bir konum seçin", Toast.LENGTH_SHORT).show()
            } else if (radius == null || radius <= 0) {
                Toast.makeText(this, "Geçerli bir yarıçap girin", Toast.LENGTH_SHORT).show()
            } else {
                updateGeofenceCircle(geofenceLocation!!, radius)
            }
        }

    // Asıl geofence’i backend’e kaydeder ve sistemde aktive eder
        btnSaveGeofence.setOnClickListener {
            val radius = etRadius.text.toString().toFloatOrNull()
            if (geofenceLocation == null || radius == null || radius <= 0) {
                Toast.makeText(this, "Önce bir konum ve geçerli yarıçap belirleyin", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Adres bulunamadı", Toast.LENGTH_SHORT).show()
                return
            }

            val result = results[0]
            val latLng = LatLng(result.latitude, result.longitude)
            geofenceLocation = latLng

            // Önceki marker'ı kaldır
            customSelectionMarker?.remove()

            // Yeni marker ekle
            customSelectionMarker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Arama Sonucu")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            customSelectionMarker?.showInfoWindow()

            // Haritayı konuma yakınlaştır
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

        } catch (e: Exception) {
            Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
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
        // 🔧 Kullanıcı dostu harita ayarları
        map.uiSettings.isZoomControlsEnabled = true      // + / - butonları
        map.uiSettings.isZoomGesturesEnabled = true      // pinch-to-zoom
        map.uiSettings.isScrollGesturesEnabled = true    // sürükleme
        map.uiSettings.isRotateGesturesEnabled = true    // iki parmakla döndürme
        map.uiSettings.isTiltGesturesEnabled = true
        // Hasta yakını haritada rastgele bir noktaya tıkladığında marker ekle
        map.setOnMapClickListener { latLng ->
            // Yeni seçilen konumu kaydet
            geofenceLocation = latLng

            // Varsa eski marker'ları kaldır (sadece geofence için olanlar)
            customSelectionMarker?.remove()

            // Yeni marker koy
            customSelectionMarker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Seçilen Konum")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )

            customSelectionMarker?.showInfoWindow()

            // Eski çemberi kaldır (setGeofence'e tıklanırsa yeniden çizilecek)
            geofenceCircle?.remove()
        }

        // Safe Location markerlarına tıklama
        map.setOnMarkerClickListener { marker ->
            val location = marker.tag
            if (location != null) {
                geofenceLocation = marker.position
                Toast.makeText(this, "Konum seçildi: ${marker.title}", Toast.LENGTH_SHORT).show()
                marker.showInfoWindow()
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

        // 🔍 Çizilen daireye zoom yap
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
                .setTitle("Konum İzni Gerekli")
                .setMessage("Uygulamanın hasta takibi ve geofence özelliğini kullanabilmesi için konum izinlerine ihtiyacı var.")
                .setPositiveButton("İzin Ver") { _, _ ->
                    ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
                }
                .setNegativeButton("İptal", null)
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
            var allGranted = true

            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false
                    break
                }
            }

            if (allGranted) {
                Toast.makeText(this, "İzinler başarıyla verildi ✅", Toast.LENGTH_SHORT).show()
                // Harita veya geofence işlemleri başlatılabilir
            } else {
                Toast.makeText(this, "İzin verilmedi ❌", Toast.LENGTH_LONG).show()

                AlertDialog.Builder(this)
                    .setTitle("İzin Gerekli")
                    .setMessage("Geofence özelliğini kullanabilmek için konum izni verilmelidir. Ayarlara gitmek ister misiniz?")
                    .setPositiveButton("Ayarlar") { _, _ ->
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = android.net.Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeButton("İptal", null)
                    .show()
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
        geofenceViewModel.saveGeofenceDetails(geofenceRequest.latitude, geofenceRequest.longitude, geofenceRequest.radius, geofenceRequest.name, geofenceRequest.patientId)
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
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            },
            onError = {
                Toast.makeText(this, "Hasta listesi yüklenemedi", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Konumlar alınamadı", Toast.LENGTH_SHORT).show()
            }
        )
    }





}
