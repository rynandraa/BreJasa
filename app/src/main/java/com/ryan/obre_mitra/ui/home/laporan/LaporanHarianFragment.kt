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
import com.ryan.obre_mitra.databinding.FragmentLaporanHarianBinding

class LaporanHarianFragment : Fragment() {

    private lateinit var binding: FragmentLaporanHarianBinding
    private val viewModel: LaporanViewModel by activityViewModels()
    private lateinit var dailyDetailAdapter: DailyDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaporanHarianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        dailyDetailAdapter = DailyDetailAdapter()
        binding.recyclerViewDailyDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDailyDetails.adapter = dailyDetailAdapter

        // Observe LiveData from ViewModel
        viewModel.dailyTransactions.observe(viewLifecycleOwner, Observer { transactions ->
            dailyDetailAdapter.setData(transactions)
        })
    }
}