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
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

//            viewModel.login(
//                email, password, role = "caregiver", // ✅ rol parametresi eklendi
//                onSuccess = {
//                    startActivity(Intent(this, CaregiverHomeActivity::class.java))
//                    finish()
//                },
//                onError = {
//                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//                })

            startActivity(Intent(this, CaregiverHomeActivity::class.java))
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
