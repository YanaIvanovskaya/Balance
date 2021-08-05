package com.example.balance.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.databinding.FragmentProfitStatisticsBinding
import com.example.balance.ui.recycler_view.adapter.StatisticsAdapter
import com.example.balance.ui.recycler_view.item.CategoryChartItem
import com.example.balance.ui.recycler_view.item.Item

class ProfitsStatisticsFragment: Fragment(R.layout.fragment_profit_statistics) {

    private var mBinding: FragmentProfitStatisticsBinding? = null
    private lateinit var profitRecyclerView: RecyclerView
    private var profitAdapter: StatisticsAdapter = StatisticsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentProfitStatisticsBinding.inflate(inflater, container, false)
        mBinding = binding
        profitRecyclerView = binding.profitStatRecyclerView
        profitRecyclerView.layoutManager = LinearLayoutManager(context)
        profitRecyclerView.adapter = profitAdapter
        return binding.root
    }

    fun setData(profitStatItems: List<Item>) {
        profitAdapter.dataSet = profitStatItems
        profitAdapter.notifyDataSetChanged()
    }

}
