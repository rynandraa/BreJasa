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


class HistorySelesaiFragment : Fragment() {

    private val listPemesanan: MutableList<RiwayatPemesanan> = mutableListOf()
    private lateinit var adapter: HistoryAdapter
    private lateinit var recyclerViewCompleted: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history_selesai, container, false)
        recyclerViewCompleted = view.findViewById(R.id.recyclerViewCompleted)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewCompleted.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(listPemesanan)
        recyclerViewCompleted.adapter = adapter

        fetchCompletedOrders()
    }

    private fun fetchCompletedOrders() {
        listPemesanan.clear()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            Firebase.firestore.collection("RiwayatPesanan")
                .whereEqualTo("idOwner", uid)
                .whereIn("statusPesanan", listOf("Selesai", "Dibatalkan"))
                .orderBy("tanggalPesanan", Query.Direction.DESCENDING)
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