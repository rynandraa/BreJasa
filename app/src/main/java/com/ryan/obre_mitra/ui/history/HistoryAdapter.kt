package com.ryan.obre_mitra.ui.history

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(private val listPemesanan: List<RiwayatPemesanan>) : RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pemesanan = listPemesanan[position]
        if (pemesanan != null) {
            holder.bind(pemesanan)
            holder.itemView.setOnClickListener {
                val moveToDetail = Intent(holder.itemView.context, HistoryDetailActivity::class.java)
                moveToDetail.putExtra("Data", pemesanan)
                moveToDetail.putExtra("IdPesanan", pemesanan.idPesanan)
                // Tambahkan log untuk memastikan ID Pesanan diteruskan
                Log.d("HistoryAdapter", "Mengirim ID Pesanan: ${pemesanan.idPesanan}")
                holder.itemView.context.startActivity(moveToDetail)
            }
        }
    }

    class MyViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pemesanan: RiwayatPemesanan) {
            binding.textNamaPelanggan.text = pemesanan.namaPelanggan
            val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            val formattedDate = pemesanan.tanggalPesanan?.let { dateFormat.format(it.toDate()) }
            binding.textTanggalPesanan.text = formattedDate
            val formattedHarga = "Rp ${pemesanan.totalBiaya.toString()}"
            binding.textHargaLayanan.text = formattedHarga
            // Set status pesanan
            binding.statusPesanan.text = pemesanan.statusPesanan
            when (pemesanan.statusPesanan) {
                "Diproses" -> {
                    binding.statusPesanan.setTextColor(ContextCompat.getColor(binding.root.context, R.color.yellow))
                }
                "Dibatalkan" -> {
                    binding.statusPesanan.setTextColor(ContextCompat.getColor(binding.root.context, R.color.danger_main))
                }
                "Selesai" -> {
                    binding.statusPesanan.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listPemesanan.size
    }
}