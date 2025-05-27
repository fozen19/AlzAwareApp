package com.example.alzawaremobile.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alzawaremobile.R
import com.example.alzawaremobile.adapters.PatientAdapter
import com.example.alzawaremobile.utils.TokenManager
import com.example.alzawaremobile.viewmodels.CaregiverPatientViewModel

class ViewPatientsActivity : AppCompatActivity() {
    private val viewModel: CaregiverPatientViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var titleTextView: TextView
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_patients)

        recyclerView = findViewById(R.id.patientRecyclerView)
        titleTextView = findViewById(R.id.titleTextView)
        backButton = findViewById(R.id.backButton)

        val caregiverId = TokenManager.getUserId(this)
        titleTextView.text = "Loading..."

        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.getPatientsByCaregiver(
            caregiverId = caregiverId,
            onSuccess = { patients ->
                titleTextView.text = "👥 Assigned Patients (${patients.size})"
                recyclerView.adapter = PatientAdapter(this, patients)
            },
            onError = {
                titleTextView.text = "Failed to load patients ❌"
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        )

        backButton.setOnClickListener {
            finish()
        }
    }
}
