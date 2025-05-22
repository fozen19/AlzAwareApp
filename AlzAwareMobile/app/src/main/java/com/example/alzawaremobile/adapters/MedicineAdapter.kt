package com.example.alzawaremobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alzawaremobile.R
import com.example.alzawaremobile.models.Medicine

class MedicineAdapter(
    private val medicines: MutableList<Medicine>,
    private val onEditClick: (Medicine) -> Unit,
    private val onDeleteClick: (Medicine) -> Unit
) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)
        return MedicineViewHolder(view)
    }

    override fun getItemCount(): Int = medicines.size

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val med = medicines[position]
        holder.nameTextView.text = med.name
        holder.daysTextView.text = "Her Gün"
        holder.dayPartsTextView.text = when (med.whichDayParts) {
            1 -> "Öğle"
            2 -> "Sabah - Akşam"
            3 -> "Sabah - Öğle - Akşam"
            else -> ""
        }
        holder.usageTextView.text = if (med.usage == 1) "Tok Karnına" else "Aç Karnına"

        holder.menuButton.setOnClickListener { anchor ->
            PopupMenu(anchor.context, anchor).apply {
                inflate(R.menu.med_edit_delete_menu)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.edit_option -> {
                            onEditClick(med)
                            true
                        }
                        R.id.delete_option -> {
                            onDeleteClick(med)
                            true
                        }
                        else -> false
                    }
                }
                show()
            }
        }
    }

    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView    = itemView.findViewById(R.id.drug_name)
        val daysTextView: TextView    = itemView.findViewById(R.id.daysTextView)
        val dayPartsTextView: TextView= itemView.findViewById(R.id.dayPartsTextView)
        val usageTextView: TextView   = itemView.findViewById(R.id.usageTextView)
        val menuButton: Button        = itemView.findViewById(R.id.button_menu)
    }
}
