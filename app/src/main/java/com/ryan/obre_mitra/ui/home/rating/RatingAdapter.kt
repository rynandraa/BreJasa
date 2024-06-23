package com.ryan.obre_mitra.ui.home.rating

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ryan.obre_mitra.databinding.ItemRatingBinding
import java.text.SimpleDateFormat
import java.util.Locale

class RatingAdapter(private val listRating: List<Rating>) : RecyclerView.Adapter<RatingAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val rating = listRating[position]
        holder.bind(rating)
    }

    class MyViewHolder(private val binding: ItemRatingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rating: Rating) {
            binding.textComment.text = rating.comment
            binding.textNamaPelanggan.text = rating.namaPelanggan
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val formattedDate = rating.tanggalPesan?.let { dateFormat.format(it.toDate()) }
            binding.textTanggalPesanan.text = formattedDate
            binding.textRatingLayanan.text = rating.rating?.toFloat().toString()
//            binding.ratingBar.rating = rating.rating!!.toFloat()
        }
    }

    override fun getItemCount(): Int {
        return listRating.size
    }
}