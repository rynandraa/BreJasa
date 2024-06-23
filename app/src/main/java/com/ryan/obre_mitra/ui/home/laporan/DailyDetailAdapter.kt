package com.ryan.obre_mitra.ui.home.laporan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ryan.obre_mitra.databinding.ItemDailyDetailBinding
import com.ryan.obre_mitra.ui.history.RiwayatPemesanan
import java.text.SimpleDateFormat
import java.util.Locale

class DailyDetailAdapter : RecyclerView.Adapter<DailyDetailAdapter.DailyDetailViewHolder>() {

    private val transactions = mutableListOf<RiwayatPemesanan>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyDetailViewHolder {
        val binding = ItemDailyDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyDetailViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
    }

    override fun getItemCount(): Int = transactions.size

    fun setData(newTransactions: List<RiwayatPemesanan>) {
        transactions.clear()
        transactions.addAll(newTransactions)
        notifyDataSetChanged()
    }

    class DailyDetailViewHolder(private val binding: ItemDailyDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: RiwayatPemesanan) {
            binding.tvOrderPrice.text = "Rp ${transaction.totalBiaya?.let { String.format("%,.0f", it.toDouble()) } ?: "0"}"
            val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            val formattedDate = transaction.tanggalPesanan?.let { dateFormat.format(it.toDate()) }
            binding.tvOrderDate.text = formattedDate
            binding.tvPaymentMethod.text = transaction.metodeBayar
        }
    }
}