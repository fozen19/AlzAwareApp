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

        // Başlık dinamik ayarlansın
        binding.tvSignupTitle.text = "Hasta Kayıt"

        binding.btnSignup.setOnClickListener {
            val username = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val phoneNumber = binding.etPhoneNumber.text.toString()

            // 🎯 Boş alan kontrolü
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Lütfen tüm zorunlu alanları doldurun.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.signup(
                username = username,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber.ifEmpty { null },
                role = "PATIENT",
                onSuccess = {
                    startActivity(Intent(this, PatientHomeActivity::class.java))
                    finish()
                },
                onError = {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, PatientLoginActivity::class.java))
            finish()
        }
    }
}
