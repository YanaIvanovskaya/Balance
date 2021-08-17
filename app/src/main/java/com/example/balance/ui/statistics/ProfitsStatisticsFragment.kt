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
import com.example.balance.databinding.FragmentProfitStatisticsBinding
import com.example.balance.ui.recycler_view.DividerItemDecoration
import com.example.balance.ui.recycler_view.adapter.StatisticsAdapter
import com.example.balance.ui.recycler_view.item.Item

class ProfitsStatisticsFragment : Fragment(R.layout.fragment_profit_statistics) {

    private var mBinding: FragmentProfitStatisticsBinding? = null
    private lateinit var mProfitRecyclerView: RecyclerView
    private var mProfitAdapter: StatisticsAdapter = StatisticsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentProfitStatisticsBinding.inflate(inflater, container, false)
        mBinding = binding
        mProfitRecyclerView = binding.profitStatRecyclerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mProfitRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        ResourcesCompat.getDrawable(resources, R.drawable.item_divider, null)
            ?.let { DividerItemDecoration(it) }
            ?.let { mProfitRecyclerView.addItemDecoration(it) }
        mProfitRecyclerView.adapter = mProfitAdapter
    }

    fun setData(profitStatItems: List<Item>) {
        mProfitAdapter.dataSet = profitStatItems
        mProfitAdapter.notifyDataSetChanged()
    }

}
