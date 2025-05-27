package com.example.alzawaremobile.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.alzawaremobile.fragments.SafeLocationFragment
import com.example.alzawaremobile.fragments.ViewLocationFragment
import com.example.alzawaremobile.fragments.ViewMedicationsFragment

class PatientDetailPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ViewMedicationsFragment()
            1 -> SafeLocationFragment()
            2 -> ViewLocationFragment()
            else -> throw IllegalStateException("Invalid tab index")
        }
    }
}
