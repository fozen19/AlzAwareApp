package com.example.alzawaremobile.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.databinding.ActivityRoleSelectionBinding

class RoleSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoleSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPatient.setOnClickListener {
            startActivity(Intent(this, PatientLoginActivity::class.java))
        }

        binding.btnCaregiver.setOnClickListener {
            startActivity(Intent(this, CaregiverLoginActivity::class.java))
        }
    }
}
