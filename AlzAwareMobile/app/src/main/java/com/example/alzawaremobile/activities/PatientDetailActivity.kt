package com.example.alzawaremobile.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.alzawaremobile.R
import com.example.alzawaremobile.fragments.PatientProfileFragment
import com.example.alzawaremobile.fragments.SafeLocationFragment
import com.example.alzawaremobile.fragments.ViewLocationFragment
import com.example.alzawaremobile.fragments.ViewMedicationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class PatientDetailActivity : AppCompatActivity() {
    private var patientId: Long = 0
    private lateinit var patientName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)

        patientId = intent.getLongExtra("patientId", -1)
        patientName = intent.getStringExtra("patientName") ?: "Hasta"

        val bottomNav = findViewById<BottomNavigationView>(R.id.patientBottomNav)

        // İlk açıldığında gösterilecek fragment
        if (savedInstanceState == null) {
            loadFragment(ViewMedicationsFragment())
        }

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.nav_view_meds -> {
                    loadFragment(ViewMedicationsFragment())
                    true
                }
                R.id.nav_safe_locations -> {
                    loadFragment(SafeLocationFragment())
                    true
                }
                R.id.nav_location -> {
                    loadFragment(ViewLocationFragment())
                    true
                }
                R.id.nav_patient_profile -> {
                    loadFragment(PatientProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putLong("patientId", patientId.toLong())
        fragment.arguments = bundle

        //hasta bilgilerini al
        fragment.arguments = Bundle().apply {
            putLong("patientId", patientId)
            putString("patientName", patientName)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.patientDetailFragmentContainer, fragment)
            .commit()
    }
}
