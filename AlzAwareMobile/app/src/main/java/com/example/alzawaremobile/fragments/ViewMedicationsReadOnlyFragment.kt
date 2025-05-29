package com.example.alzawaremobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alzawaremobile.R
import com.example.alzawaremobile.adapters.MedicineAdapter
import com.example.alzawaremobile.models.Medicine
import com.example.alzawaremobile.utils.TokenManager
import com.example.alzawaremobile.viewmodels.MedicineViewModel

class ViewMedicationsReadOnlyFragment : Fragment() {

    private val viewModel: MedicineViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicineAdapter
    private val medicines = mutableListOf<Medicine>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_view_medications_readonly, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.medicinesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = MedicineAdapter(
            medicines,
            onEditClick = {},
            onDeleteClick = {},
            isReadOnly = true
        )
        recyclerView.adapter = adapter

        val patientId = TokenManager.getUserId(requireContext())
        viewModel.setPatientId(patientId)
        viewModel.refreshList()

        viewModel.medicines.observe(viewLifecycleOwner) {
            medicines.clear()
            medicines.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }
}
