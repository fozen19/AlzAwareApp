package com.example.alzawaremobile.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.alzawaremobile.R
import com.example.alzawaremobile.fragments.AddMedicationFragment
import com.example.alzawaremobile.fragments.CreateGeofenceFragment
import com.example.alzawaremobile.fragments.ViewLocationFragment
import com.example.alzawaremobile.fragments.ViewMedicationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class PatientDetailActivity : AppCompatActivity() {
    private lateinit var patientId: String
    private lateinit var patientName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)

        patientId = intent.getLongExtra("patientId", -1).toString()
        patientName = intent.getStringExtra("patientName") ?: "Hasta"

        val bottomNav = findViewById<BottomNavigationView>(R.id.patientBottomNav)

        // İlk açıldığında gösterilecek fragment
        if (savedInstanceState == null) {
            loadFragment(AddMedicationFragment())
        }

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_add_med -> {
                    loadFragment(AddMedicationFragment())
                    true
                }
                R.id.nav_view_meds -> {
                    loadFragment(ViewMedicationsFragment())
                    true
                }
                R.id.nav_geofence -> {
                    loadFragment(CreateGeofenceFragment())
                    true
                }
                R.id.nav_location -> {
                    loadFragment(ViewLocationFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.patientDetailFragmentContainer, fragment)
            .commit()
    }
}
