package com.ryan.obre_mitra.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}