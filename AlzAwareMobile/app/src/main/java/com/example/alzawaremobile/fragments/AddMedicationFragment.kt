package com.example.alzawaremobile.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.alzawaremobile.R

class AddMedicationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_medication, container, false)

        val medicationEditText: EditText = view.findViewById(R.id.medicationEditText)
        val assignButton: Button = view.findViewById(R.id.assignMedicationButton)

        assignButton.setOnClickListener {
            val medication = medicationEditText.text.toString()
            if (medication.isNotBlank()) {
                Toast.makeText(requireContext(), "İlaç atandı: $medication", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "İlaç adı boş olamaz", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
