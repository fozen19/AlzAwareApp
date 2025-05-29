package com.example.alzawaremobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.alzawaremobile.R
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.utils.TokenManager
import com.example.alzawaremobile.viewmodels.PatientViewModel

class PatientProfileReadOnlyFragment : Fragment() {

    private val viewModel: PatientViewModel by viewModels()

    private lateinit var etPatientId: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNumber: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_patient_profile_readonly, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etPatientId = view.findViewById(R.id.etPatientId)
        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)
        etUsername = view.findViewById(R.id.etUsername)
        etEmail = view.findViewById(R.id.etEmail)
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber)

        val patientId = TokenManager.getUserId(requireContext())
        viewModel.fetchPatientProfile(patientId)

        viewModel.patientProfile.observe(viewLifecycleOwner) { user ->
            bindProfile(user)
        }
    }

    private fun bindProfile(user: User) {
        etPatientId.setText(user.id.toString())
        etFirstName.setText(user.firstName)
        etLastName.setText(user.lastName)
        etUsername.setText(user.username)
        etEmail.setText(user.email)
        etPhoneNumber.setText(user.phoneNumber ?: "")

        // Disable all fields
        listOf(
            etPatientId, etFirstName, etLastName, etUsername, etEmail, etPhoneNumber
        ).forEach { it.isEnabled = false }
    }
}
