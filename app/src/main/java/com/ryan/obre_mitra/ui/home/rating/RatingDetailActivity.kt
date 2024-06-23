package com.ryan.obre_mitra.ui.home.rating

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.ryan.obre_mitra.databinding.ActivityRatingDetailBinding

class RatingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRatingDetailBinding
    private lateinit var adapter: RatingAdapter
    private val listRating: MutableList<Rating> = mutableListOf()
    private var documentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingDetailBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        documentId = intent.getStringExtra("DocumentId")

        binding.recyclerViewRating.layoutManager = LinearLayoutManager(this)
        adapter = RatingAdapter(listRating)
        binding.recyclerViewRating.adapter = adapter

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        fetchRatingData()
    }

    private fun fetchRatingData() {
        documentId?.let { id ->
            FirebaseFirestore.getInstance().collection("LayananJasa")
                .document(id)
                .collection("Rating")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val rating = document.toObject(Rating::class.java)
                        rating?.let { listRating.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    println("Error fetching data: $e")
                }
        }
    }
}