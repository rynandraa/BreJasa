package com.ryan.obre_mitra.ui.home.jasa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ryan.obre_mitra.databinding.ActivityMenuJasaBinding

class MenuJasaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuJasaBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JasaAdapter
    private val listLayananJasa: MutableList<LayananJasa> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuJasaBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        recyclerView = binding.rvMenujasa
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = JasaAdapter(listLayananJasa)
        recyclerView.adapter = adapter

        // Ambil data layanan dari Firebase Firestore jika pengguna sudah masuk
        if (FirebaseAuth.getInstance().currentUser != null) {
            fetchDataFromFirestore()
        } else {
            Log.d("Debug", "Gagal")
        }
        binding.btnBack.setOnClickListener{
            onBackPressed()
        }
        binding.floatingButton.setOnClickListener {
            // Intent untuk memulai TambahJasaActivity
            val intent = Intent(this, TambahJasaActivity::class.java)
            startActivity(intent)
        }
    }


    private fun fetchDataFromFirestore() {
        // Bersihkan listPemesanan sebelum menambahkan data baru
        listLayananJasa.clear()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            Firebase.firestore.collection("LayananJasa")
                .whereEqualTo("idOwner", uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // Loop melalui setiap dokumen dan tambahkan data pemesanan ke dalam list
                    for (document in querySnapshot.documents) {
                        val jasa = document.toObject(LayananJasa::class.java)?.copy(documentId = document.id)
                        jasa?.let {
                            listLayananJasa.add(it)
                        }
                    }
                    // Setelah data diperbarui, beritahu adapter untuk memperbarui tampilan
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    // Tampilkan pesan error jika gagal mengambil data dari Firestore
                    println("Error fetching data: $e")
                }
        }
    }

}