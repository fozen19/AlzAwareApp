package com.example.alzawaremobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.databinding.ActivityCaregiverLoginBinding
import com.example.alzawaremobile.viewmodels.AuthViewModel

class CaregiverLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCaregiverLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaregiverLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val userName = binding.etUserName.text.toString()
            val password = binding.etPassword.text.toString()

            if (userName.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Proceed with login
            viewModel.login(
                userName, password, role = "CAREGIVER",
                onSuccess = { userId ->
                    val intent = Intent(this, CaregiverHomeActivity::class.java)
                    intent.putExtra("caregiverId", userId)
                    startActivity(intent)
                    finish()
                },
                onError = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.tvGoToSignup.setOnClickListener {
            startActivity(Intent(this, CaregiverSignupActivity::class.java))
        }

        binding.tvGoToRoleSelection.setOnClickListener {
            startActivity(Intent(this, RoleSelectionActivity::class.java))
            finish()
        }
    }
}
