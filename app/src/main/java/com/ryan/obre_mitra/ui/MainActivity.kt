package com.ryan.obre_mitra.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavController()

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.beranda -> {
                    if (navController.currentDestination?.id != R.id.homeFragment) {
                        navController.popBackStack(R.id.homeFragment, false)
                        navController.navigate(R.id.homeFragment)
                    }
                }
                R.id.riwayat -> {
                    if (navController.currentDestination?.id != R.id.historyFragment) {
                        navController.navigate(R.id.historyFragment)
                    }
                }
                R.id.profile -> {
                    if (navController.currentDestination?.id != R.id.profileFragment) {
                        navController.navigate(R.id.profileFragment)
                    }
                }
            }
            true
        }

        mainViewModel.userData.observe(this, { user ->
            user?.let {
                val welcomeMessage = "Selamat datang kembali, ${user.fullname}!"
                Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "Gagal mengambil data pengguna dari Firestore", Toast.LENGTH_SHORT).show()
            }
        })

        // Fetch user data
        mainViewModel.fetchUserData()

        // Handle Intent to navigate to specific fragment
        handleIntent()
    }

    private fun handleIntent() {
        val fragmentName = intent.getStringExtra("FragmentName")
        if (fragmentName == "HistoryFragment") {
            navController.navigate(R.id.historyFragment)
            binding.bottomNavigationView.selectedItemId = R.id.riwayat
        }
        if (fragmentName == "Profil Fragment") {
            navController.navigate(R.id.profileFragment)
            binding.bottomNavigationView.selectedItemId = R.id.profile
        }
    }

    private fun setupNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id != R.id.homeFragment) {
            navController.popBackStack(R.id.homeFragment, false)
            binding.bottomNavigationView.selectedItemId = R.id.beranda
        } else {
            super.onBackPressed()
        }
    }
}
