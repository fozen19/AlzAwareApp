package com.example.alzawaremobile.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.alzawaremobile.R
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.viewmodels.PatientViewModel

class PatientProfileFragment : Fragment() {

    private val viewModel: PatientViewModel by viewModels()

    private lateinit var etPatientId: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNumber: EditText

    private var patientId: Long = -1L
    private var currentUser: User? = null
    private var listenerEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_patient_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        patientId = arguments?.getLong("patientId") ?: -1

        etPatientId = view.findViewById(R.id.etPatientId)
        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)
        etUsername = view.findViewById(R.id.etUsername)
        etEmail = view.findViewById(R.id.etEmail)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber)

        setupObservers()
        viewModel.fetchPatientProfile(patientId)
    }

    private fun setupObservers() {
        viewModel.patientProfile.observe(viewLifecycleOwner) { user ->
            currentUser = user
            listenerEnabled = false
            etPatientId.setText(user.id.toString())
            etFirstName.setText(user.firstName)
            etLastName.setText(user.lastName)
            etUsername.setText(user.username)
            etEmail.setText(user.email)
            etPhoneNumber.setText(user.phoneNumber ?: "")
            setupTextWatchers()
            listenerEnabled = true
        }

        viewModel.updateResult.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupTextWatchers() {
        etFirstName.addTextChangedListener(watcher { text -> updateUser { it.copy(firstName = text) } })
        etLastName.addTextChangedListener(watcher { text -> updateUser { it.copy(lastName = text) } })
        etUsername.addTextChangedListener(watcher { text -> updateUser { it.copy(username = text) } })
        etEmail.addTextChangedListener(watcher { text -> updateUser { it.copy(email = text) } })
        etPhoneNumber.addTextChangedListener(watcher { text -> updateUser { it.copy(phoneNumber = text) } })
    }

    private fun watcher(onChange: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (listenerEnabled && s != null) onChange(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun updateUser(update: (User) -> User) {
        val updatedUser = currentUser?.let(update) ?: return
        currentUser = updatedUser
        viewModel.updatePatientProfile(patientId, updatedUser)
    }
}
