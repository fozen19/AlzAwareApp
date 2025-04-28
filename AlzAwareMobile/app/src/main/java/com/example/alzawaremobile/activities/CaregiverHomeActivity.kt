package com.example.alzawaremobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.alzawaremobile.R
import com.example.alzawaremobile.databinding.ActivityCaregiverHomeBinding

class CaregiverHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCaregiverHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaregiverHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
