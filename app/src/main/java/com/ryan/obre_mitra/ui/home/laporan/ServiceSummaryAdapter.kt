package com.ryan.obre_mitra.ui.home.laporan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ryan.obre_mitra.databinding.ItemServiceSummaryBinding

data class ServiceSummary(val serviceName: String, val orderCount: Int)

class ServiceSummaryAdapter : RecyclerView.Adapter<ServiceSummaryAdapter.ServiceSummaryViewHolder>() {

    private val summaries = mutableListOf<ServiceSummary>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceSummaryViewHolder {
        val binding = ItemServiceSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceSummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceSummaryViewHolder, position: Int) {
        val summary = summaries[position]
        holder.bind(summary)
    }

    override fun getItemCount(): Int = summaries.size

    fun setData(newSummaries: List<ServiceSummary>) {
        summaries.clear()
        summaries.addAll(newSummaries)
        notifyDataSetChanged()
    }

    class ServiceSummaryViewHolder(private val binding: ItemServiceSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(summary: ServiceSummary) {
            binding.tvServiceName.text = summary.serviceName
            binding.tvOrderCount.text = summary.orderCount.toString()
        }
    }
}
