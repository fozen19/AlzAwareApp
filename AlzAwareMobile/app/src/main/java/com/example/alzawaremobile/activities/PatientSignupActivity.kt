package com.example.alzawaremobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.databinding.ActivityPatientSignupBinding
import com.example.alzawaremobile.viewmodels.AuthViewModel

class PatientSignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientSignupBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val caregiverId = binding.etCaregiverId.text.toString()

            viewModel.signupPatient(name, email, password, caregiverId,
                onSuccess = {
                    startActivity(Intent(this, PatientHomeActivity::class.java))
                    finish()
                },
                onError = {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                })
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, PatientLoginActivity::class.java))
            finish()
        }
    }
}
