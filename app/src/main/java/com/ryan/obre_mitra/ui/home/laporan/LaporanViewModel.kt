package com.ryan.obre_mitra.ui.home.laporan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.ryan.obre_mitra.ui.history.RiwayatPemesanan
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale

class LaporanViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _dailyTransactions = MutableLiveData<List<RiwayatPemesanan>>()
    val dailyTransactions: LiveData<List<RiwayatPemesanan>> = _dailyTransactions

    private val _serviceSummary = MutableLiveData<List<ServiceSummary>>()
    val serviceSummary: LiveData<List<ServiceSummary>> = _serviceSummary

    private val _totalPenghasilan = MutableLiveData<Long>()
    val totalPenghasilan: LiveData<Long> = _totalPenghasilan

    fun fetchTransactionDataForMonth(month: Month) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w("LaporanViewModel", "User not logged in")
            return
        }

        val startOfMonth = LocalDate.of(LocalDate.now().year, month, 1)
        val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)

        val startTimestamp = Timestamp(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(), 0)
        val endTimestamp = Timestamp(endOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toEpochSecond(), 0)

        db.collection("RiwayatPesanan")
            .whereEqualTo("idOwner", userId)
            .whereEqualTo("statusPesanan", "Selesai")
            .orderBy("tanggalPesanan", Query.Direction.DESCENDING)
            .whereGreaterThanOrEqualTo("tanggalPesanan", startTimestamp)
            .whereLessThanOrEqualTo("tanggalPesanan", endTimestamp)
            .get()
            .addOnSuccessListener { documents ->
                val transactions = documents.mapNotNull { it.toObject(RiwayatPemesanan::class.java) }

                // Sort transactions by tanggalPesanan
                val sortedTransactions = transactions.sortedBy { it.tanggalPesanan }

                _dailyTransactions.value = sortedTransactions
                calculateServiceSummary(transactions)
                calculateTotalPenghasilan(transactions)
            }
            .addOnFailureListener { exception ->
                Log.e("LaporanViewModel", "Error getting documents: ", exception)
            }
    }

    private fun calculateServiceSummary(transactions: List<RiwayatPemesanan>) {
        val summaryMap = mutableMapOf<String, Int>()

        for (transaction in transactions) {
            val serviceName = transaction.namaLayanan ?: "Unknown Service"
            summaryMap[serviceName] = (summaryMap[serviceName] ?: 0) + 1
        }

        val summaryList = summaryMap.map { ServiceSummary(it.key, it.value) }
        _serviceSummary.value = summaryList
    }

    private fun calculateTotalPenghasilan(transactions: List<RiwayatPemesanan>) {
        val total = transactions.mapNotNull { it.totalBiaya?.toLong() }.sum()
        _totalPenghasilan.value = total
    }
}