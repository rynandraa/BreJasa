package com.ryan.obre_mitra.ui.home.jasa

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.databinding.ItemJasaBinding

class JasaAdapter(private val listLayananJasa: List<LayananJasa>) : RecyclerView.Adapter<JasaAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemJasaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val jasa = listLayananJasa[position]
        if (jasa != null) {
            holder.bind(jasa)
            holder.itemView.setOnClickListener {
                val moveToDetail = Intent(holder.itemView.context, EditJasaActivity::class.java)
                moveToDetail.putExtra("Data", jasa)
                moveToDetail.putExtra("DocumentId", jasa.documentId) // Kirim document ID melalui Intent
                holder.itemView.context.startActivity(moveToDetail)
            }
        }
    }

    class MyViewHolder(private val binding: ItemJasaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(jasa: LayananJasa) {
            binding.namaLayananTextView.text = jasa.namaLayanan
            binding.hargaTextView.text = "Rp. ${jasa.biayaJasa}"
            binding.deskripsiTextView.text = jasa.deskripsi
            Glide.with(binding.imageService.context)
                .load(jasa.photoUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.imageService)
        }
    }

    override fun getItemCount(): Int {
        return listLayananJasa.size
    }
}