package com.ryan.obre_mitra.ui.home.rating

import android.content.Intent
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.databinding.ItemLayananBinding
import com.ryan.obre_mitra.ui.home.jasa.LayananJasa


class LayananAdapter(private val listLayanan: List<LayananJasa>) : RecyclerView.Adapter<LayananAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemLayananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val layanan = listLayanan[position]
        if (layanan != null) {
            holder.bind(layanan)
            holder.itemView.setOnClickListener {
                val moveToDetail = Intent(holder.itemView.context, RatingDetailActivity::class.java)
                moveToDetail.putExtra("DocumentId", layanan.documentId) // Kirim document ID melalui Intent
                holder.itemView.context.startActivity(moveToDetail)
            }
        }
    }

    class MyViewHolder(private val binding: ItemLayananBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(layanan: LayananJasa) {
            binding.textNamaLayanan.text = layanan.namaLayanan
            binding.textKategoriLayanan.text = layanan.kategori

            // Menggunakan Glide untuk memuat gambar
            Glide.with(binding.imageLayanan.context)
                .load(layanan.photoUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.imageLayanan)

            // Menghitung dan menampilkan rata-rata rating
            layanan.documentId?.let { documentId ->
                FirebaseFirestore.getInstance().collection("LayananJasa")
                    .document(documentId)
                    .collection("Rating")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val totalRating = querySnapshot.documents.sumByDouble { it.getDouble("rating") ?: 0.0 }
                        val averageRating = if (querySnapshot.size() > 0) totalRating / querySnapshot.size() else 0.0
                        binding.textRatingLayanan.text = "%.1f".format(averageRating)
                    }
                    .addOnFailureListener { e ->
                        binding.textRatingLayanan.text = "Rating: N/A"
                    }
            } ?: run {
                binding.textRatingLayanan.text = "Rating: N/A"
            }
        }
    }

    override fun getItemCount(): Int {
        return listLayanan.size
    }
}