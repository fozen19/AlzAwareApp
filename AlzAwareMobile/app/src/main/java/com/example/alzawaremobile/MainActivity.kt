package com.example.alzawaremobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.activities.*
import com.example.alzawaremobile.utils.TokenManager
import android.os.Build
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.Manifest

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_NOTIFICATION_PERMISSION = 1001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATION_PERMISSION
                )
            }
        }

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
