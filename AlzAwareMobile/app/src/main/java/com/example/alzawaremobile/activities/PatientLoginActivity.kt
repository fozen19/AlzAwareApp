package com.example.alzawaremobile.activities

import android.content.Intent
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

        binding.btnLogin.setOnClickListener {
            val userName = binding.etUserName.text.toString()
            val password = binding.etPassword.text.toString()

            if (userName.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Username and password must not be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🧠 PATIENT login with ID forwarding
            viewModel.login(
                userName,
                password,
                role = "PATIENT",
                onSuccess = { userId ->
                    val intent = Intent(this, PatientHomeActivity::class.java)
                    intent.putExtra("patientId", userId)
                    startActivity(intent)
                    finish()
                },
                onError = {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            )
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
