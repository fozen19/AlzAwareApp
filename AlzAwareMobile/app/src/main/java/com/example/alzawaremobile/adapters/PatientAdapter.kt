package com.example.alzawaremobile.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alzawaremobile.R
import com.example.alzawaremobile.activities.PatientDetailActivity
import com.example.alzawaremobile.models.User

class PatientAdapter(
    private val context: Context,
    private val patients: List<User>
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientNameTextView: TextView = itemView.findViewById(R.id.patientNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        holder.patientNameTextView.text = "${patient.firstName} ${patient.lastName}"
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PatientDetailActivity::class.java).apply {
                putExtra("patientId", patient.id)
                putExtra("patientName", "${patient.firstName} ${patient.lastName}")
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = patients.size
}
