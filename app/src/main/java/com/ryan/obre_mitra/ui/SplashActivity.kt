package com.ryan.obre_mitra.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 3000 // 3 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        Handler().postDelayed({
            // Mulai main activity setelah waktu tunggu
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }
}