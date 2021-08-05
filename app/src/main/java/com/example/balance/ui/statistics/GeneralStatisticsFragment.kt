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
import com.example.balance.databinding.FragmentGeneralStatisticsBinding
import com.example.balance.getMonthName
import com.example.balance.presentation.GeneralStatisticsState
import com.example.balance.presentation.GeneralStatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlin.math.abs

class GeneralStatisticsFragment : Fragment(R.layout.fragment_general_statistics) {

    private var mBinding: FragmentGeneralStatisticsBinding? = null

    private val mViewModel by getViewModel {
        GeneralStatisticsViewModel(
            recordRepository = BalanceApp.recordRepository
        )
    }

    private lateinit var mCommonBarChart: BarChart
    private lateinit var mRestBarChart: BarChart
    private lateinit var chart: PieChart

    private val onCommonChartValueSelectedListener = object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e is BarEntry) {
                val month = getMonthName(e.data as Int, Case.IN)
                val sumMoney = e.yVals[h!!.stackIndex].toInt()
                val message =
                    "В $month ${if (sumMoney > 0) "получено" else "потрачено"} ${abs(sumMoney)} P"
                mBinding?.descriptionChartCommon?.text = message
            }

        }

        override fun onNothingSelected() {}
    }

    private val onRestChartValueSelectedListener = object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e is BarEntry) {
                val month = getMonthName(e.data as Int, Case.IN)
                val sumMoney = e.y.toInt()
                val message =
                    "В $month ${
                        if (sumMoney > 0) "получена прибыль"
                        else "получен убыток"
                    } - ${abs(sumMoney)} P"
                mBinding?.descriptionChartRest?.text = message
            }
        }

        override fun onNothingSelected() {}
    }

    private val xAxisFormatter: ValueFormatter = object : ValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val currentEntry = mCommonBarChart.data.dataSets[0].getEntriesForXValue(value)[0]
            return getMonthName(currentEntry.data as Int, Case.SHORT)
        }

    }

    private val yAxisFormatter: ValueFormatter = object : ValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return if (abs(value) < 1000f) {
                "${value.toInt()}"
            } else "${(value / 1000).toInt()}K"
        }

        override fun getFormattedValue(value: Float): String {
            return getAxisLabel(value, null)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGeneralStatisticsBinding.inflate(inflater, container, false)
        mBinding = binding
        mCommonBarChart = binding.commonChart
        mRestBarChart = binding.restChart
        chart = binding.pieChart
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        createCommonChart()
        createRestChart()
//        createPieChart()
        return binding.root

    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun render(state: GeneralStatisticsState) {
        updateCommonChart(state.entriesCommonChart)
        updateRestChart(state.entriesRestChart)
    }

    private fun createCommonChart() {
        mCommonBarChart.setOnChartValueSelectedListener(onCommonChartValueSelectedListener)
        mCommonBarChart.setDrawGridBackground(false)
        mCommonBarChart.description.isEnabled = false
        mCommonBarChart.setFitBars(true)
        mCommonBarChart.setDrawValueAboveBar(true)
        mCommonBarChart.animateX(500)
        mCommonBarChart.animateY(700)

        mCommonBarChart.axisRight.isEnabled = false
        mCommonBarChart.axisLeft.setDrawGridLines(false)
        mCommonBarChart.axisLeft.setDrawZeroLine(true)
        mCommonBarChart.axisLeft.setLabelCount(7, false)
        mCommonBarChart.axisLeft.textSize = 12f
        mCommonBarChart.axisLeft.valueFormatter = yAxisFormatter

        val xAxis = mCommonBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 12f
        xAxis.granularity = 1f
        xAxis.valueFormatter = xAxisFormatter

        val l: Legend = mCommonBarChart.legend
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.formSize = 8f
        l.formToTextSpace = 4f
        l.xEntrySpace = 6f
    }

    private fun updateCommonChart(barEntries: List<BarEntry>) {
        if (barEntries.isNullOrEmpty()) {
            return
        }

        val set = BarDataSet(barEntries, "")
        set.setDrawIcons(false)
        set.valueTextSize = 11f
        set.valueFormatter = yAxisFormatter
        set.setColors(
            Color.rgb(255, 100, 100),
            Color.rgb(100, 255, 100)
        )
        set.stackLabels = arrayOf(
            "Расходы", "Доходы"
        )

        val data = BarData(set)
        mCommonBarChart.data = data
        println(data.dataSets)
        mCommonBarChart.setVisibleXRangeMaximum(6f)
        mCommonBarChart.invalidate()
    }

    private fun createRestChart() {
        mRestBarChart.setOnChartValueSelectedListener(onRestChartValueSelectedListener)
        mRestBarChart.setDrawValueAboveBar(true)
        mRestBarChart.description.isEnabled = false
        mRestBarChart.setDrawGridBackground(false)
        mRestBarChart.setFitBars(true)
        mRestBarChart.animateX(500)
        mRestBarChart.animateY(700)

        mRestBarChart.axisRight.isEnabled = false
        mRestBarChart.axisLeft.setDrawGridLines(false)
        mRestBarChart.axisLeft.setDrawZeroLine(true)
        mRestBarChart.axisLeft.setLabelCount(7, false)
        mRestBarChart.axisLeft.textSize = 12f
        mRestBarChart.axisLeft.valueFormatter = yAxisFormatter

        val xAxis = mRestBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 12f
        xAxis.granularity = 1f
        xAxis.valueFormatter = xAxisFormatter

        mRestBarChart.legend.isEnabled = false
    }

    private fun updateRestChart(barEntries: List<BarEntry>) {
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
        mRestBarChart.data = data
        mRestBarChart.invalidate()
    }



//    private fun createPieChart() {
//
//        chart.setUsePercentValues(true)
//        chart.getDescription().setEnabled(false)
//        chart.setExtraOffsets(5f, 10f, 5f, 5f)
//
//        chart.setDragDecelerationFrictionCoef(0.95f)
//
//        chart.setCenterText("Profit")
//
//        chart.setDrawHoleEnabled(true)
//        chart.setHoleColor(Color.WHITE)
//
//        chart.setTransparentCircleColor(Color.WHITE)
//        chart.setTransparentCircleAlpha(110)
//
//        chart.setHoleRadius(58f)
//        chart.setTransparentCircleRadius(61f)
//
//        chart.setDrawCenterText(true)
//
//        chart.rotationAngle = 0f
//        chart.isRotationEnabled = true
//        chart.isHighlightPerTapEnabled = true
//        // add a selection listener
////        chart.setOnChartValueSelectedListener(this)
//
//        chart.animateY(1400, Easing.EaseInOutQuad)
//
//        val l: Legend = chart.getLegend()
//        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
//        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
//        l.orientation = Legend.LegendOrientation.HORIZONTAL
//        l.setDrawInside(true)
//        l.isWordWrapEnabled = true
//        l.xEntrySpace = 7f
//        l.textSize = 12f
//        l.yEntrySpace = 0f
//        l.yOffset = 0f
//
//        chart.setEntryLabelColor(Color.WHITE)
//        chart.setEntryLabelTextSize(12f)
//        setData(15,10f)
//    }

//    private fun setData(count: Int, range: Float) {
//        val entries = java.util.ArrayList<PieEntry>()
//
//        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
//        // the chart.
//        var summ = 0f
//        for (i in 0 until count) {
//            val value = (Math.random() * range + range / 5).toFloat()
//            summ += value
//            entries.add(
//                PieEntry(
//                    value,"category$i"
//                )
//            )
//        }
//
//        val dataSet = PieDataSet(entries, "Election Results")
//        dataSet.setDrawIcons(false)
//        dataSet.sliceSpace = 3f
//        dataSet.iconsOffset = MPPointF(0f, 40f)
//        dataSet.selectionShift = 5f
//
//        // add a lot of colors
//        val colors = java.util.ArrayList<Int>()
//        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
//        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
//        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
//        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
//        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
//        colors.add(ColorTemplate.getHoloBlue())
//        dataSet.colors = colors
//
//        val data = PieData(dataSet)
//        data.setValueFormatter(PercentFormatter())
//        data.setValueTextSize(11f)
//        data.setValueTextColor(Color.WHITE)
//
//        chart.data = data
//        chart.highlightValues(null)
//        chart.invalidate()
//    }
}

