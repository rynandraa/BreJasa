package com.ryan.obre_mitra.ui.home.laporan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ryan.obre_mitra.R
import com.ryan.obre_mitra.databinding.FragmentLaporanJasaBinding

class LaporanJasaFragment : Fragment() {

    private lateinit var binding: FragmentLaporanJasaBinding
    private val viewModel: LaporanViewModel by activityViewModels()
    private lateinit var serviceSummaryAdapter: ServiceSummaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaporanJasaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        serviceSummaryAdapter = ServiceSummaryAdapter()
        binding.recyclerViewServiceSummary.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewServiceSummary.adapter = serviceSummaryAdapter

        // Observe LiveData from ViewModel
        viewModel.serviceSummary.observe(viewLifecycleOwner, Observer { summary ->
            serviceSummaryAdapter.setData(summary)
        })
    }
}