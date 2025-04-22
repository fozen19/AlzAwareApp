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

        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            viewModel.signupCaregiver(name, email, password,
                onSuccess = {
                    startActivity(Intent(this, CaregiverHomeActivity::class.java))
                    finish()
                },
                onError = {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                })
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, CaregiverLoginActivity::class.java))
            finish()
        }
    }
}
