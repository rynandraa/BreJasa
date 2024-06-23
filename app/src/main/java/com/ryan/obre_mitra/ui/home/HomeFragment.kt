package com.ryan.obre_mitra.ui.home

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.ui.home.jasa.MenuJasaActivity
import com.ryan.obre_mitra.ui.home.laporan.LaporanActivity
import com.ryan.obre_mitra.ui.home.rating.RatingActivity
import com.ryan.obre_mitra.ui.FormActivity


class HomeFragment : Fragment() {

    private lateinit var floatingButton: FloatingActionButton
    private lateinit var tambahData: TextView
    private lateinit var textNamaUsaha: TextView
    private lateinit var textJumlahPesanan: TextView
    private lateinit var textPenghasilanHariIni: TextView

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val firebasedb = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val sharedPref = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        floatingButton = view.findViewById(R.id.floating_button)
        tambahData = view.findViewById(R.id.tambah_data)
        textNamaUsaha = view.findViewById(R.id.text_nama_usaha)
        textJumlahPesanan = view.findViewById(R.id.text_jumlah_pesanan)
        textPenghasilanHariIni = view.findViewById(R.id.text_penghasilan_hari_ini)

        if (userId != null) {
            firebasedb.collection("UserMitra").document(userId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            val namaUsaha = document.getString("namaUsaha")
                            if (namaUsaha.isNullOrEmpty()) {
                                // Tampilkan FloatingActionButton dan TextView
                                floatingButton.visibility = View.VISIBLE
                                tambahData.visibility = View.VISIBLE
                                // Sembunyikan teks lain di CardView
                                textNamaUsaha.visibility = View.GONE
                                textJumlahPesanan.visibility = View.GONE
                                textPenghasilanHariIni.visibility = View.GONE
                            } else {
                                // Sembunyikan FloatingActionButton dan TextView
                                floatingButton.visibility = View.GONE
                                tambahData.visibility = View.GONE
                                // Tampilkan teks lain di CardView
                                textNamaUsaha.visibility = View.VISIBLE
                                textJumlahPesanan.visibility = View.VISIBLE
                                textPenghasilanHariIni.visibility = View.VISIBLE
                                // Set nilai teks dari Firestore
                                textNamaUsaha.text = namaUsaha
                                // Panggil fungsi untuk menghitung penghasilan dan jumlah pesanan hari ini
                                viewModel.fetchDailyIncome()
                                viewModel.fetchJumlahPesananHariIni()
                            }
                        }
                    } else {
                        // Handle error
                    }
                }
            floatingButton.setOnClickListener {
                val intent = Intent(activity, FormActivity::class.java)
                startActivity(intent)
            }
        }

        userId?.let { uid ->
            val userRef = firebasedb.collection("UserMitra").document(uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("fullname")
                        val userPhone = document.getString("phoneNumber")

                        Log.d("Debug", "Username: $userName")
                        Log.d("Debug", "Userphone: $userPhone")

                        with(sharedPref.edit()) {
                            putString("USER_NAME", userName)
                            putString("USER_PHONENUMBER", userPhone)
                            apply()
                        }

                        view.findViewById<TextView>(R.id.text_nama_pengguna)?.text = userName
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting document: $exception")
                }
        }

        view.findViewById<CardView>(R.id.card_menu)?.setOnClickListener {
            val intent = Intent(requireContext(), MenuJasaActivity::class.java)
            startActivity(intent)
        }
        view.findViewById<CardView>(R.id.card_rating)?.setOnClickListener {
            val intent = Intent(requireContext(), RatingActivity::class.java)
            startActivity(intent)
        }
        view.findViewById<CardView>(R.id.card_laporan)?.setOnClickListener {
            val intent = Intent(requireContext(), LaporanActivity::class.java)
            startActivity(intent)
        }

        // Observe LiveData from ViewModel
        viewModel.dailyIncome.observe(viewLifecycleOwner, Observer { dailyIncome ->
            textPenghasilanHariIni.text = "Penghasilan Hari Ini: Rp ${dailyIncome?.let { formatCurrency(it) } ?: "0"}"
        })

        viewModel.jumlahPesananHariIni.observe(viewLifecycleOwner, Observer { jumlahPesanan ->
            textJumlahPesanan.text = "Jumlah Pesanan Hari Ini: $jumlahPesanan"
        })
    }

    // Function to format currency
    private fun formatCurrency(amount: Double): String {
        return String.format("%,.0f", amount)
    }
}