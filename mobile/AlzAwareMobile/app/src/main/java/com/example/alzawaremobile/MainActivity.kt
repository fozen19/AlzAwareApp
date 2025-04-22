package com.example.alzawaremobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.activities.*
import com.example.alzawaremobile.utils.TokenManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = TokenManager.getToken(this)
        val role = TokenManager.getUserRole(this)

        val intent = when {
            token.isNullOrEmpty() || role.isNullOrEmpty() -> {
                Intent(this, RoleSelectionActivity::class.java)
            }

            role == "patient" -> {
                Intent(this, PatientHomeActivity::class.java)
            }

            role == "caregiver" -> {
                Intent(this, CaregiverHomeActivity::class.java)
            }

            else -> {
                Intent(this, RoleSelectionActivity::class.java)
            }
        }

        startActivity(intent)
        finish()
    }
}
