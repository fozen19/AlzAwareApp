package com.example.alzawaremobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View

class MedicationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication)

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val userRole = sharedPref.getString("user_role", "ROLE_USER")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener  { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                // diğer ekranları ekle
                else -> false
            }
        }

        val menuButton: Button = findViewById(R.id.button_menu)
        val addButton: Button = findViewById(R.id.add_button)

        if (userRole == "ROLE_CAREGIVER") {
            menuButton.visibility = View.VISIBLE
            addButton.visibility = View.VISIBLE
        } else
            menuButton.visibility = View.GONE
            addButton.visibility = View.GONE
        }

    //devam ettt
}
