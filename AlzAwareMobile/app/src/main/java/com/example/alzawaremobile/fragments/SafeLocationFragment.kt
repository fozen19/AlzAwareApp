package com.example.alzawaremobile.fragments

import android.location.Geocoder
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.alzawaremobile.R
import com.example.alzawaremobile.viewmodels.SafeLocationViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.util.*

class SafeLocationFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: SafeLocationViewModel by viewModels()
    private lateinit var map: GoogleMap
    private lateinit var etLocationName: EditText
    private lateinit var etSearchAddress: EditText
    private lateinit var btnSearchAddress: Button
    private lateinit var btnSave: Button
    private var selectedLatLng: LatLng? = null
    private var selectedMarker: Marker? = null
    private var patientId: Long = -1L
    private val existingMarkers = mutableListOf<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_safe_location, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        patientId = arguments?.getLong("patientId") ?: -1L

        etSearchAddress = view.findViewById(R.id.etSearchAddress)
        btnSearchAddress = view.findViewById(R.id.btnSearchAddress)
        etLocationName = view.findViewById(R.id.etLocationName)
        btnSave = view.findViewById(R.id.btnSaveLocation)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapSafeLocation) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnSearchAddress.setOnClickListener {
            searchAddress()
        }

        etSearchAddress.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                searchAddress()
                true
            } else false
        }

        btnSave.setOnClickListener {
            val name = etLocationName.text.toString()
            val point = selectedLatLng

            if (name.isBlank() || point == null) {
                Toast.makeText(requireContext(), "Lütfen isim ve konum seçin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addSafeLocation(
                patientId, name, point.latitude, point.longitude,
                onSuccess = {
                    Toast.makeText(requireContext(), "Konum kaydedildi ✅", Toast.LENGTH_SHORT).show()
                    etLocationName.text.clear()
                    selectedMarker?.remove()
                    selectedLatLng = null
                    loadSafeLocations()
                },
                onError = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun searchAddress() {
        val query = etSearchAddress.text.toString()
        if (query.isBlank()) {
            Toast.makeText(requireContext(), "Lütfen bir adres girin", Toast.LENGTH_SHORT).show()
            return
        }

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val results = geocoder.getFromLocationName(query, 1)
            if (results.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Adres bulunamadı", Toast.LENGTH_SHORT).show()
                return
            }

            val result = results[0]
            val latLng = LatLng(result.latitude, result.longitude)
            selectedLatLng = latLng

            selectedMarker?.remove()
            selectedMarker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Arama Sonucu")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            selectedMarker?.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            // 📌 Adres metnini konum adı alanına otomatik yaz
            val addressLines = buildString {
                for (i in 0..result.maxAddressLineIndex) {
                    append(result.getAddressLine(i))
                    if (i != result.maxAddressLineIndex) append(", ")
                }
            }
            etLocationName.setText(addressLines)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isScrollGesturesEnabled = true
        map.uiSettings.isZoomControlsEnabled = true

        map.setOnMapClickListener { latLng ->
            selectedLatLng = latLng
            selectedMarker?.remove()
            selectedMarker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Yeni Seçilen Konum")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            selectedMarker?.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }

        map.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true
        }

        map.setOnInfoWindowLongClickListener { marker ->
            val locationId = marker.tag as? Long ?: return@setOnInfoWindowLongClickListener
            AlertDialog.Builder(requireContext())
                .setTitle("Konumu Sil")
                .setMessage("${marker.title} adlı konumu silmek istiyor musunuz?")
                .setPositiveButton("Evet") { _, _ ->
                    viewModel.deleteSafeLocation(locationId,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Konum silindi ✅", Toast.LENGTH_SHORT).show()
                            loadSafeLocations()
                        },
                        onError = {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        })
                }
                .setNegativeButton("Hayır", null)
                .show()
        }

        loadSafeLocations()
    }

    private fun loadSafeLocations() {
        viewModel.getSafeLocationsByPatient(
            patientId,
            onSuccess = { locations ->
                existingMarkers.forEach { it.remove() }
                existingMarkers.clear()

                for (location in locations) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(location.locationName)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    )
                    marker?.tag = location.id
                    marker?.let { existingMarkers.add(it) }
                }

                if (locations.isNotEmpty()) {
                    val first = locations.first()
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(first.latitude, first.longitude), 13f))
                }
            },
            onError = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
