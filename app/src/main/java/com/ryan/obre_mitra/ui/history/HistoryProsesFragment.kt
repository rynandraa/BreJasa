package com.ryan.obre_mitra.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.ryan.obre_mitra.R

class HistoryProsesFragment : Fragment() {

    private val listPemesanan: MutableList<RiwayatPemesanan> = mutableListOf()
    private lateinit var adapter: HistoryAdapter
    private lateinit var recyclerViewProcessing: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history_proses, container, false)
        recyclerViewProcessing = view.findViewById(R.id.recyclerViewProcessing)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewProcessing.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(listPemesanan)
        recyclerViewProcessing.adapter = adapter

        fetchProcessingOrders()
    }

    private fun fetchProcessingOrders() {
        listPemesanan.clear()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            Firebase.firestore.collection("RiwayatPesanan")
                .whereEqualTo("idOwner", uid)
                .whereEqualTo("statusPesanan", "Diproses")
                .orderBy("tanggalPesanan", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val pemesanan = document.toObject(RiwayatPemesanan::class.java)?.copy(idPesanan = document.id)
                        pemesanan?.let {
                            listPemesanan.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    println("Error fetching data: $e")
                }
        }
    }
}