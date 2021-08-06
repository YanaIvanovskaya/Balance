package com.example.balance.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentProfitLossChartBinding
import com.example.balance.presentation.GeneralStatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class ProfitLossChartFragment : Fragment(R.layout.fragment_profit_loss_chart) {

    private lateinit var mProfitLossBarChart: BarChart
    private var mBinding: FragmentProfitLossChartBinding? = null
    private val mViewModel by getViewModel {
        GeneralStatisticsViewModel(
            recordRepository = BalanceApp.recordRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentProfitLossChartBinding.inflate(inflater, container, false)
        mBinding = binding
        mProfitLossBarChart = binding.profitLossChart
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createProfitLossBarChart()
        mViewModel.state.observe(viewLifecycleOwner, {
            updateProfitLossChart(it.entriesProfitLossBarChart)
        })
    }

    override fun onDestroyView() {
        println("ProfitLossChartFragment onDestroyView")
        mBinding = null
        super.onDestroyView()
    }

    private fun createProfitLossBarChart() {
        //mProfitLossBarChart.setOnChartValueSelectedListener(onRestChartValueSelectedListener)
        mProfitLossBarChart.setDrawValueAboveBar(true)
        mProfitLossBarChart.description.isEnabled = false
        mProfitLossBarChart.setDrawGridBackground(false)
        mProfitLossBarChart.setFitBars(true)
        mProfitLossBarChart.animateX(500)
        mProfitLossBarChart.animateY(700)

        mProfitLossBarChart.axisRight.isEnabled = false
        mProfitLossBarChart.axisLeft.setDrawGridLines(false)
        mProfitLossBarChart.axisLeft.setDrawZeroLine(true)
        mProfitLossBarChart.axisLeft.setLabelCount(7, false)
        mProfitLossBarChart.axisLeft.textSize = 12f
        mProfitLossBarChart.axisLeft.valueFormatter = YAxisFormatter()

        val xAxis = mProfitLossBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 12f
        xAxis.granularity = 1f
        xAxis.valueFormatter = XAxisFormatter(mProfitLossBarChart)
        mProfitLossBarChart.legend.isEnabled = false
    }

    private fun updateProfitLossChart(barEntries: List<BarEntry>) {
        if (barEntries.isNullOrEmpty()) {
            return
        }

        val colors: MutableList<Int> = ArrayList()
        val green = Color.rgb(110, 190, 102)
        val red = Color.rgb(211, 74, 88)

        barEntries.forEach {
            if (it.y <= 0) colors.add(red) else colors.add(green)
        }

        val set = BarDataSet(barEntries, "")
        set.colors = colors
        set.setValueTextColors(colors)

        val data = BarData(set)
        data.setValueTextSize(13f)
        mProfitLossBarChart.data = data
        mProfitLossBarChart.invalidate()
    }

}