package com.example.alzawaremobile.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.R
import com.example.alzawaremobile.databinding.ActivityCaregiverHomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CaregiverHomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityCaregiverHomeBinding
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaregiverHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvWelcome.text = "Hoş geldiniz!, Hasta Yakını"

        // Get the SupportMapFragment and request the map.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}