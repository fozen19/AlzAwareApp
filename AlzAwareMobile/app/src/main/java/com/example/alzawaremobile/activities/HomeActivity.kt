package com.example.alzawaremobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val userRole = sharedPref.getString("user_role", "ROLE_USER")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener  { item ->
            when (item.itemId) {
                R.id.medication -> {
                    startActivity(Intent(this, MedicationActivity::class.java))
                    true
                }
                // diğer ekranları ekle
                else -> false
            }
        }
    }
}
