package com.example.balance.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.balance.Case
import com.example.balance.R
import com.example.balance.databinding.FragmentGeneralChartBinding
import com.example.balance.formatAsSum
import com.example.balance.getMonthName
import com.example.balance.presentation.getViewModel
import com.example.balance.presentation.statistics.GeneralChartViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.util.Collections.max

class GeneralChartFragment : Fragment(R.layout.fragment_general_chart) {

    private lateinit var mGeneralBarChart: BarChart
    private var mBinding: FragmentGeneralChartBinding? = null
    private val mViewModel by getViewModel {
        GeneralChartViewModel()
    }
    private val onValueSelectedListener = object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e is BarEntry) {
                val data = e.data
                val sumMoney = e.yVals[h!!.stackIndex].toInt()
                val message = if (data is List<*>) {
                    val monthNumber = data[0] as Int
                    val month = getMonthName(monthNumber, Case.SHORT).uppercase()
                    val year = data[1].toString()
                    "$month $year  ${sumMoney.formatAsSum()} ₽"
                } else ""
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
            updateGeneralBarChart(it.entries)
            mBinding?.preloaderGeneralChart?.isVisible = !it.isContentLoaded
        })
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun createGeneralBarChart() {
        mGeneralBarChart.apply {
            setOnChartValueSelectedListener(onValueSelectedListener)
            setExtraOffsets(5f, 10f, 5f, 10f)
            setDrawGridBackground(false)
            setFitBars(true)
            setDrawValueAboveBar(true)
            setNoDataText("Пока тут ничего нет")
            setNoDataTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.grey_800,
                    null
                )
            )
            setXAxisRenderer(
                CustomXAxisRenderer(
                    mGeneralBarChart.viewPortHandler,
                    mGeneralBarChart.xAxis,
                    mGeneralBarChart.getTransformer(YAxis.AxisDependency.LEFT)
                )
            )
            description.isEnabled = false
            renderer = RoundedBarChartRenderer(
                this,
                animator,
                viewPortHandler
            ).apply { setRadius(20) }
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            legend.isEnabled = false
            animateX(500)
            animateY(700)
        }

        mGeneralBarChart.xAxis.apply {
            position = XAxis.XAxisPosition.TOP
            setDrawGridLines(false)
            setDrawAxisLine(false)
            textSize = 11f
            granularity = 1f
            yOffset = 20f
            valueFormatter = XAxisFormatter(mGeneralBarChart)
        }
    }

    private fun updateGeneralBarChart(barEntries: List<BarEntry>) {
        if (barEntries.isNullOrEmpty()) {
            return
        }
        val set = BarDataSet(barEntries, "").apply {
            valueTextSize = 12f
            valueFormatter = BarValueFormatter()
            setColors(
                ResourcesCompat.getColor(resources, R.color.red_200, null),
                ResourcesCompat.getColor(resources, R.color.green_200, null)
            )
        }
        mGeneralBarChart.apply {
            data = BarData(set)
            setVisibleXRangeMaximum(6f)
            moveViewToX(max(barEntries.map { it.x }))
        }
    }

}