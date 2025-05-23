package com.example.alzawaremobile.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alzawaremobile.R
import com.example.alzawaremobile.adapters.MedicineAdapter
import com.example.alzawaremobile.models.Medicine
import com.example.alzawaremobile.models.User
import com.example.alzawaremobile.viewmodels.MedicineViewModel
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class ViewMedicationsFragment : Fragment() {
    private val viewModel: MedicineViewModel by viewModels()
    private var patientId: Long = -1L
    private var patientName: String = ""

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicineAdapter
    private val medicines = mutableListOf<Medicine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            patientId = it.getLong("patientId", -1L)
            patientName = it.getString("patientName", "") ?: ""
        }
        viewModel.setPatientId(patientId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_medications, container, false)

        val nameTextView = view.findViewById<TextView>(R.id.patientNameTextView)
        nameTextView.text = patientName

        val backButton = view.findViewById<Button>(R.id.backButtonPatient)
        backButton.setOnClickListener {
            requireActivity().finish()
        }

        recyclerView = view.findViewById(R.id.medicinesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = MedicineAdapter(
            medicines,
            onEditClick = { med -> showAddEditDialog(med) },
            onDeleteClick = { med -> deleteMedicine(med) }
        )
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.add_button)
        fab.setOnClickListener {
            showAddEditDialog(null)
        }

        observeViewModel(view)

        return view
    }

    private fun observeViewModel(rootView: View) {
        viewModel.medicines.observe(viewLifecycleOwner) { meds ->
            medicines.clear()
            medicines.addAll(meds)
            adapter.notifyDataSetChanged()
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG).show()
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { msg ->
            Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun showAddEditDialog(med: Medicine?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_medicine, null)

        val medicineName = dialogView.findViewById<EditText>(R.id.medicineName)
        val dayParts   = dialogView.findViewById<MaterialButtonToggleGroup>(R.id.medication_dayParts)
        val usage     = dialogView.findViewById<MaterialButtonToggleGroup>(R.id.medication_usage)
        val count     = dialogView.findViewById<EditText>(R.id.medicine_count)

        if (med != null) {
            med?.let {
                medicineName.setText(it.name)
                when (it.inMorning) {
                    1 -> {
                        dayParts.check(dayParts.getChildAt(1).id)
                    }
                }

                when (it.inAfternoon) {
                    1 -> {
                        dayParts.check(dayParts.getChildAt(2).id)
                    }
                }

                when (it.inEvening) {
                    1 -> {
                        dayParts.check(dayParts.getChildAt(3).id)
                    }
                }


                if (it.usage == 0) {
                    usage.check(usage.getChildAt(0).id)
                } else {
                    usage.check(usage.getChildAt(1).id)
                }
                count.setText(it.count.toString())
            }
        }

        val title = if (med == null) "Yeni İlaç Ekle" else "İlaç Düzenle"
        val buttonText = if (med == null) "Kaydet" else "Düzenle"

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setNegativeButton("İptal", null)
            .setPositiveButton(buttonText, null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = medicineName.text.toString().trim()
            if (name.isEmpty()) {
                medicineName.error = "İsim alanı boş bırakılamaz."
                return@setOnClickListener
            }

            val checkedDayParts = dayParts.checkedButtonIds
            val inMorning = when {
                checkedDayParts.contains(dayParts.getChildAt(0).id) -> 1
                else -> 0
            }

            val inAfternoon = when {
                checkedDayParts.contains(dayParts.getChildAt(1).id) -> 1
                else -> 0
            }

            val inEvening = when {
                checkedDayParts.contains(dayParts.getChildAt(2).id) -> 1
                else -> 0
            }

            val checkedUsage = usage.checkedButtonId
            val usage = if (checkedUsage == usage.getChildAt(0).id) 0 else 1

            val count = count.text.toString().toIntOrNull() ?: 0

            if (med == null) {
                val newMed = Medicine(
                    id = null,
                    name = name,
                    inMorning = inMorning,
                    inAfternoon = inAfternoon,
                    inEvening = inEvening,
                    usage = usage,
                    count = count,
                    patient = User(
                        id = patientId,
                        username = "",   //bunları da halledecek
                        email = "",
                        firstName = "",
                        lastName = "",
                        phoneNumber = ""
                    )
                )
                viewModel.createMedicine(newMed)

                view?.let {
                    Snackbar.make(it, "İlaç eklendi.", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                val updated = med.copy(
                    name = name,
                    inMorning = inMorning,
                    inAfternoon = inAfternoon,
                    inEvening = inEvening,
                    usage = usage,
                    count = count
                )
                med.id?.let { it1 -> viewModel.updateMedicine(it1, updated) }

                view?.let {
                    Snackbar.make(it, "İlaç güncellendi.", Snackbar.LENGTH_SHORT).show()
                }
            }

            dialog.dismiss()
            viewModel.refreshList()

        }

    }

    private fun deleteMedicine(med: Medicine) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("İlaç Silinsin mi?")
            .setMessage("“${med.name}” ilacını silmek istediğinize emin misiniz?")
            .setNegativeButton("İptal", null)
            .setPositiveButton("Sil") { _, _ ->
                // 1) Perform the deletion only after confirmation
                med.id?.let { viewModel.deleteMedicine(it) }
                // 2) Show feedback that it was deleted
                view?.let {
                    Snackbar.make(it, "İlaç silindi.", Snackbar.LENGTH_SHORT).show()
                }
            }
            .show()
    }


}
