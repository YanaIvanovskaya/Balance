package com.example.balance.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.databinding.FragmentCostsStatisticsBinding
import com.example.balance.ui.recycler_view.DividerItemDecoration
import com.example.balance.ui.recycler_view.adapter.StatisticsAdapter
import com.example.balance.ui.recycler_view.item.Item

class CostsStatisticsFragment :
    Fragment(R.layout.fragment_profit_statistics) {

    private var mBinding: FragmentCostsStatisticsBinding? = null
    private var mCostsAdapter: StatisticsAdapter = StatisticsAdapter()
    private lateinit var mCostsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCostsStatisticsBinding.inflate(inflater, container, false)
        mBinding = binding
        mCostsRecyclerView = binding.costsStatRecyclerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCostsRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        ResourcesCompat.getDrawable(resources, R.drawable.item_divider, null)
            ?.let { DividerItemDecoration(it) }
            ?.let { mCostsRecyclerView.addItemDecoration(it) }
        mCostsRecyclerView.adapter = mCostsAdapter
    }

    fun setData(costStatItems: List<Item>) {
        mCostsAdapter.dataSet = costStatItems
        mCostsAdapter.notifyDataSetChanged()
    }

}