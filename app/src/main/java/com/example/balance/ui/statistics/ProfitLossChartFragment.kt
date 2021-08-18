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
import com.example.balance.databinding.FragmentProfitLossChartBinding
import com.example.balance.formatAsSum
import com.example.balance.getMonthName
import com.example.balance.presentation.getViewModel
import com.example.balance.presentation.statistics.ProfitLossChartViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.util.*
import kotlin.collections.ArrayList

class ProfitLossChartFragment : Fragment(R.layout.fragment_profit_loss_chart) {

    private lateinit var mProfitLossBarChart: BarChart
    private var mBinding: FragmentProfitLossChartBinding? = null
    private val mViewModel by getViewModel {
        ProfitLossChartViewModel()
    }
    private val onValueSelectedListener = object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e is BarEntry) {
                val data = e.data
                val sumMoney = e.y.toInt()
                val message = if (data is List<*>) {
                    val monthNumber = data[0] as Int
                    val month = getMonthName(monthNumber, Case.SHORT).uppercase()
                    val year = data[1].toString()
                    "$month $year  ${sumMoney.formatAsSum()} ₽"
                } else ""
                mBinding?.resumeProfitLossChart?.text = message
            }
        }

        override fun onNothingSelected() {}
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
            updateProfitLossChart(it.entries)
            mBinding?.preloaderProfitLossChart?.isVisible = !it.isContentLoaded
        })
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun createProfitLossBarChart() {
        mProfitLossBarChart.apply {
            setOnChartValueSelectedListener(onValueSelectedListener)
            setExtraOffsets(5f, 10f, 5f, 30f)
            setDrawGridBackground(false)
            setFitBars(true)
            setDrawValueAboveBar(true)
            animateX(500)
            animateY(700)
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
                    mProfitLossBarChart.viewPortHandler,
                    mProfitLossBarChart.xAxis,
                    mProfitLossBarChart.getTransformer(YAxis.AxisDependency.LEFT)
                )
            )
            setVisibleXRangeMaximum(6f)
            description.isEnabled = false
            renderer = RoundedBarChartRenderer(
                mProfitLossBarChart,
                mProfitLossBarChart.animator,
                mProfitLossBarChart.viewPortHandler
            ).apply { setRadius(20) }
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            legend.isEnabled = false
        }

        mProfitLossBarChart.xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            textSize = 11f
            granularity = 1f
            yOffset = 40f
            valueFormatter = XAxisFormatter(mProfitLossBarChart)
            position = XAxis.XAxisPosition.TOP
        }
    }

    private fun updateProfitLossChart(barEntries: List<BarEntry>) {
        if (barEntries.isNullOrEmpty()) {
            return
        }
        val colors: MutableList<Int> = ArrayList()
        val green = ResourcesCompat.getColor(resources, R.color.green_200, null)
        val red = ResourcesCompat.getColor(resources, R.color.red_200, null)

        barEntries.forEach {
            if (it.y <= 0) colors.add(red) else colors.add(green)
        }
        val negativeValues = barEntries.filter { it.y < 0 }
        val positiveValues = barEntries.filter { it.y >= 0 }
        val hasDifferentValues = positiveValues.isNotEmpty() && negativeValues.isNotEmpty()

        if (!hasDifferentValues) {
            if (negativeValues.isNotEmpty()) {
                mProfitLossBarChart.axisLeft.axisMaximum = 0f
            } else {
                mProfitLossBarChart.axisLeft.axisMinimum = 0f
            }
        }
        val set = BarDataSet(barEntries, "").apply {
            valueTextSize = 12f
            this.colors = colors
            valueTextColor = ResourcesCompat.getColor(resources, R.color.grey_800, null)
            valueFormatter = BarValueFormatter()
        }
        mProfitLossBarChart.apply {
            data = BarData(set)
            setVisibleXRangeMaximum(6f)
            moveViewToX(Collections.max(barEntries.map { it.x }))
        }
    }

}