package com.example.alzawaremobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.alzawaremobile.R

class ViewMedicationsFragment : Fragment() {

    private val dummyMeds = listOf("Parol", "Aferin", "Augmentin")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_medications, container, false)

        val medListView: ListView = view.findViewById(R.id.medListView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dummyMeds)
        medListView.adapter = adapter

        return view
    }
}
