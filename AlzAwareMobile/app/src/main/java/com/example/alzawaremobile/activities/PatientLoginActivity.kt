package com.example.alzawaremobile.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.databinding.ActivityPatientLoginBinding
import com.example.alzawaremobile.viewmodels.AuthViewModel

class PatientLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Başlığı ayarlayalım

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // 🎯 Boş alan kontrolü
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email ve şifre boş olamaz.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Burada doğru role gönderiyoruz artık: patient
            viewModel.login(email, password, role = "patient",
                onSuccess = {
                    startActivity(Intent(this, PatientHomeActivity::class.java))
                    finish()
                },
                onError = {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                })
        }

        binding.tvGoToSignup.setOnClickListener {
            startActivity(Intent(this, PatientSignupActivity::class.java))
        }

        binding.tvGoToRoleSelection.setOnClickListener {
            startActivity(Intent(this, RoleSelectionActivity::class.java))
            finish()
        }
    }
}
