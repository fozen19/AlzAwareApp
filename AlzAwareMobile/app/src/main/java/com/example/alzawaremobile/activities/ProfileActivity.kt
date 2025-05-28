package com.example.alzawaremobile.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.R
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.utils.TokenManager
import com.example.alzawaremobile.viewmodels.CaregiverViewModel

class ProfileActivity : AppCompatActivity() {

    private val viewModel: CaregiverViewModel by viewModels()

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNumber: EditText

    private var currentUser: User? = null
    private var caregiverId: Long = -1L
    private var listenerEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        caregiverId = TokenManager.getUserId(this)

        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        findViewById<Button>(R.id.etBtnBack)?.setOnClickListener {
            finish() // Aktiviteyi kapat ve geri dön
        }

        setupObservers()
        viewModel.fetchCaregiverProfile(caregiverId)
    }

    private fun setupObservers() {
        viewModel.caregiverProfile.observe(this) { user ->
            currentUser = user
            listenerEnabled = false
            etFirstName.setText(user.firstName)
            etLastName.setText(user.lastName)
            etUsername.setText(user.username)
            etEmail.setText(user.email)
            etPhoneNumber.setText(user.phoneNumber ?: "")
            setupTextWatchers()
            listenerEnabled = true
        }

        viewModel.updateResult.observe(this) {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupTextWatchers() {
        etFirstName.addTextChangedListener(createWatcher { text -> updateUser { it.copy(firstName = text) } })
        etLastName.addTextChangedListener(createWatcher { text -> updateUser { it.copy(lastName = text) } })
        etUsername.addTextChangedListener(createWatcher { text -> updateUser { it.copy(username = text) } })
        etEmail.addTextChangedListener(createWatcher { text -> updateUser { it.copy(email = text) } })
        etPhoneNumber.addTextChangedListener(createWatcher { text -> updateUser { it.copy(phoneNumber = text) } })
    }

    private fun createWatcher(onTextChanged: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (listenerEnabled && s != null) {
                    onTextChanged(s.toString())
                }
            }
        }
    }

    private fun updateUser(update: (User) -> User) {
        val updatedUser = currentUser?.let(update) ?: return
        currentUser = updatedUser
        viewModel.updateCaregiverProfile(caregiverId, updatedUser)
    }
}
