package com.example.alzawaremobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.alzawaremobile.R
import com.example.alzawaremobile.viewmodels.SafeLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SafeLocationFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: SafeLocationViewModel by viewModels()
    private lateinit var map: GoogleMap
    private lateinit var etLocationName: EditText
    private lateinit var btnSave: Button
    private var selectedLatLng: LatLng? = null
    private var patientId: Long = -1L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_safe_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        patientId = arguments?.getLong("patientId") ?: -1L

        etLocationName = view.findViewById(R.id.etLocationName)
        btnSave = view.findViewById(R.id.btnSaveLocation)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapSafeLocation) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnSave.setOnClickListener {
            val name = etLocationName.text.toString()
            val point = selectedLatLng

            if (name.isBlank() || point == null) {
                Toast.makeText(requireContext(), "Lütfen isim ve konum seçin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addSafeLocation(patientId, name, point.latitude, point.longitude,
                onSuccess = {
                    Toast.makeText(requireContext(), "Konum kaydedildi ✅", Toast.LENGTH_SHORT).show()
                },
                onError = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isScrollGesturesEnabled = true
        map.uiSettings.isZoomControlsEnabled = true

        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Seçilen Konum"))
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            selectedLatLng = latLng
        }

        // 👇 Daha önceki safe location'ları göster
        viewModel.getSafeLocationsByPatient(patientId,
            onSuccess = { locations ->
                for (location in locations) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(location.locationName)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    )
                }

                // 📍 İlk konumu merkeze al
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
