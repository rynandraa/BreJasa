package com.ryan.obre_mitra.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ryan.obre_mitra.ui.history.RiwayatPemesanan
import java.time.LocalDate
import java.time.ZoneId

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _dailyIncome = MutableLiveData<Double>()
    val dailyIncome: LiveData<Double> get() = _dailyIncome

    private val _jumlahPesananHariIni = MutableLiveData<Int>()
    val jumlahPesananHariIni: LiveData<Int> get() = _jumlahPesananHariIni

    fun fetchDailyIncome() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w("LaporanViewModel", "User not logged in")
            return
        }
        db.collection("RiwayatPesanan")
            .whereEqualTo("statusPesanan", "Selesai")
            .whereEqualTo("idOwner", userId)
            .get()
            .addOnSuccessListener { result ->
                val transactions = result.toObjects(RiwayatPemesanan::class.java)
                val dailyIncome = calculateDailyIncome(transactions)

                // Log value for debugging
                Log.d("LaporanViewModel", "Daily Income: $dailyIncome")

                // Update LiveData
                _dailyIncome.value = dailyIncome
            }
            .addOnFailureListener { exception ->
                Log.w("LaporanViewModel", "Error getting documents: ", exception)
            }
    }

    private fun calculateDailyIncome(transactions: List<RiwayatPemesanan>): Double {
        val today = LocalDate.now()
        return transactions.filter { it.tanggalPesanan?.toLocalDate() == today }
            .sumOf { it.totalBiaya?.toDouble() ?: 0.0 }
    }

    fun fetchJumlahPesananHariIni() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w("HomeViewModel", "User not logged in")
            return
        }
        db.collection("RiwayatPesanan")
            .whereEqualTo("idOwner", userId)
            .get()
            .addOnSuccessListener { result ->
                val transactions = result.toObjects(RiwayatPemesanan::class.java)
                val jumlahPesananHariIni = calculateJumlahPesananHariIni(transactions)

                // Log value for debugging
                Log.d("HomeViewModel", "Jumlah Pesanan Hari Ini: $jumlahPesananHariIni")

                // Update LiveData
                _jumlahPesananHariIni.value = jumlahPesananHariIni
            }
            .addOnFailureListener { exception ->
                Log.w("HomeViewModel", "Error getting documents: ", exception)
            }
    }

    private fun calculateJumlahPesananHariIni(transactions: List<RiwayatPemesanan>): Int {
        val today = LocalDate.now()
        return transactions.count { it.tanggalPesanan?.toLocalDate() == today }
    }

    // Extension function to convert Timestamp to LocalDate
    private fun Timestamp.toLocalDate(): LocalDate {
        return this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }
}