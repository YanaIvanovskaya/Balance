package com.example.balance.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.balance.BalanceApp
import com.example.balance.Case
import com.example.balance.R
import com.example.balance.databinding.FragmentGeneralChartBinding
import com.example.balance.getMonthName
import com.example.balance.presentation.GeneralStatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlin.math.abs

class GeneralChartFragment : Fragment(R.layout.fragment_general_chart) {

    private lateinit var mGeneralBarChart: BarChart
    private var mBinding: FragmentGeneralChartBinding? = null
    private val mViewModel by getViewModel {
        GeneralStatisticsViewModel(
            recordRepository = BalanceApp.recordRepository
        )
    }

    private val onValueSelectedListener = object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e is BarEntry) {
                val month = getMonthName(e.data as Int, Case.NONE).uppercase()
                val sumMoney = e.yVals[h!!.stackIndex].toInt()
                val message = "$month  $sumMoney P"
                mBinding?.resumeGeneralChart?.text = message
            }
        }

        override fun onNothingSelected() {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGeneralChartBinding.inflate(inflater, container, false)
        mBinding = binding
        mGeneralBarChart = binding.generalChart
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createGeneralBarChart()
        mViewModel.state.observe(viewLifecycleOwner, {
            val entries = it.entriesGeneralBarChart
            updateGeneralBarChart(entries)
            if (entries.isNotEmpty() || (!it.haveCosts && !it.haveProfits))
                mBinding?.preloaderGeneralChart?.visibility = View.GONE
        })
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun createGeneralBarChart() {
        mGeneralBarChart.setOnChartValueSelectedListener(onValueSelectedListener)
        mGeneralBarChart.setDrawGridBackground(false)
        mGeneralBarChart.description.isEnabled = false
        mGeneralBarChart.setFitBars(true)
        mGeneralBarChart.setDrawValueAboveBar(true)
        mGeneralBarChart.animateX(500)
        mGeneralBarChart.animateY(700)

        mGeneralBarChart.axisRight.isEnabled = false
        mGeneralBarChart.axisLeft.setDrawGridLines(false)
        mGeneralBarChart.axisLeft.setDrawZeroLine(true)
        mGeneralBarChart.axisLeft.setLabelCount(7, false)
        mGeneralBarChart.axisLeft.textSize = 12f
        mGeneralBarChart.axisLeft.valueFormatter = YAxisFormatter()

        val xAxis = mGeneralBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 12f
        xAxis.granularity = 1f
        xAxis.valueFormatter = XAxisFormatter(mGeneralBarChart)
        mGeneralBarChart.legend.isEnabled = false
    }

    private fun updateGeneralBarChart(barEntries: List<BarEntry>) {
        if (barEntries.isNullOrEmpty()) {
            return
        }

        val set = BarDataSet(barEntries, "")
        set.setDrawIcons(false)
        set.valueTextSize = 11f
        set.valueFormatter = BarValueFormatter()
        set.setColors(
            Color.rgb(255, 100, 100),
            Color.rgb(100, 255, 100)
        )
        set.stackLabels = arrayOf(
            "Расходы", "Доходы"
        )

        val data = BarData(set)
        mGeneralBarChart.data = data
        println(data.dataSets)
        mGeneralBarChart.setVisibleXRangeMaximum(6f)
        mGeneralBarChart.invalidate()
    }

}