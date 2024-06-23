package com.ryan.obre_mitra.ui.home.rating

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.ryan.obre_mitra.databinding.ActivityRatingBinding
import com.ryan.obre_mitra.ui.home.jasa.LayananJasa

class RatingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRatingBinding
    private lateinit var adapter: LayananAdapter
    private lateinit var recyclerView: RecyclerView
    private val listLayanan: MutableList<LayananJasa> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }
        recyclerView = binding.recyclerViewLayanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LayananAdapter(listLayanan)
        recyclerView.adapter = adapter


        fetchLayananData()
    }

    private fun fetchLayananData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            Firebase.firestore.collection("LayananJasa")
                .whereEqualTo("idOwner", uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val layanan = document.toObject(LayananJasa::class.java)?.copy(documentId = document.id)
                        layanan?.let { listLayanan.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    println("Error fetching data: $e")
                }
        }
    }
}