package com.ryan.obre_mitra.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ryan.obre_mitra.R


class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> HistoryProsesFragment()
                    1 -> HistorySelesaiFragment()
                    else -> throw IllegalStateException("Invalid position $position")
                }
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Diproses"
                1 -> "Selesai"
                else -> null
            }
        }.attach()
    }
}