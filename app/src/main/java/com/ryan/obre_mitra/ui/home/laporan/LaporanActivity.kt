package com.ryan.obre_mitra.ui.home.laporan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.ryan.obre_mitra.databinding.ActivityLaporanBinding
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale


class LaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaporanBinding
    private val viewModel: LaporanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            // Kembali ke activity sebelumnya
            onBackPressed()
        }

        // Setup ViewPager2 and TabLayout
        binding.viewPager.adapter = LaporanPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Laporan Harian"
                1 -> "Laporan Jasa"
                else -> null
            }
        }.attach()

        // Setup AutoCompleteTextView for month selection
        val months = Month.values().map { it.getDisplayName(TextStyle.FULL, Locale.getDefault()) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, months)
        binding.autoCompleteMonth.setAdapter(adapter)

        // Set default selection to current month
        val currentMonth = LocalDate.now().month
        binding.autoCompleteMonth.setText(currentMonth.getDisplayName(TextStyle.FULL, Locale.getDefault()), false)

        binding.autoCompleteMonth.setOnItemClickListener { parent, view, position, id ->
            val selectedMonth = Month.values()[position]
            viewModel.fetchTransactionDataForMonth(selectedMonth)
        }

        // Fetch initial transaction data for the current month
        viewModel.fetchTransactionDataForMonth(currentMonth)

        // Observe totalPenghasilan and update TextView
        viewModel.totalPenghasilan.observe(this) { totalPenghasilan ->
            binding.tvTotalPenghasilan.text = "Total Penghasilan: Rp $totalPenghasilan"
        }
    }
}