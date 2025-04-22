package com.example.alzawaremobile.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.databinding.ActivityPatientHomeBinding

class PatientHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvWelcome.text = "Hoş geldiniz, Hasta!"
    }
}
