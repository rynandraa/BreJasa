package com.ryan.obre_mitra.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.databinding.ActivityAboutUsBinding
import com.ryan.obre_mitra.databinding.ActivityContactUsBinding

class ContactUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}