package com.example.alzawaremobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.databinding.ActivityCaregiverSignupBinding
import com.example.alzawaremobile.viewmodels.AuthViewModel

class CaregiverSignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCaregiverSignupBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaregiverSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dynamically set the title
        binding.tvSignupTitle.text = "Caregiver Signup"

        binding.btnSignup.setOnClickListener {
            val username = binding.etUserName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val phoneNumber = binding.etPhoneNumber.text.toString()

            // 🎯 Minimal required field check
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.signup(
                username = username,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber.ifEmpty { null },
                role = "CAREGIVER",
                onSuccess = {
                    startActivity(Intent(this, CaregiverHomeActivity::class.java))
                    finish()
                },
                onError = {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, CaregiverLoginActivity::class.java))
            finish()
        }
    }
}
