package com.example.balance.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.databinding.FragmentCostsStatisticsBinding
import com.example.balance.ui.recycler_view.adapter.StatisticsAdapter
import com.example.balance.ui.recycler_view.item.CategoryChartItem
import com.example.balance.ui.recycler_view.item.Item

class CostsStatisticsFragment :
    Fragment(R.layout.fragment_profit_statistics) {

    private var mBinding: FragmentCostsStatisticsBinding? = null
    private var costsAdapter: StatisticsAdapter = StatisticsAdapter()
    private lateinit var costsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCostsStatisticsBinding.inflate(inflater, container, false)
        mBinding = binding
        costsRecyclerView = binding.costsStatRecyclerView
        costsRecyclerView.layoutManager = LinearLayoutManager(context)
        costsRecyclerView.adapter = costsAdapter
        return binding.root
    }

    fun setData(costStatItems: List<Item>) {
        costsAdapter.dataSet = costStatItems
        costsAdapter.notifyDataSetChanged()
    }

}