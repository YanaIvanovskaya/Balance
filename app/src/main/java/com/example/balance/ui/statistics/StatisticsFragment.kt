package com.example.balance.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.databinding.FragmentStatisticsBinding
import com.example.balance.presentation.AppBarEntry
import com.example.balance.presentation.StatisticsState
import com.example.balance.presentation.StatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import kotlin.math.abs

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private var mBinding: FragmentStatisticsBinding? = null
    private lateinit var mNavController: NavController

    private val mViewModel by getViewModel {
        StatisticsViewModel(
            recordRepository = BalanceApp.recordRepository
        )
    }
    private lateinit var mCommonBarChart: BarChart
    private lateinit var mRestBarChart: BarChart

    private val onCommonChartValueSelectedListener = object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e is BarEntry) {
                val months = mapOf(
                    1 to "январе",
                    2 to "феврале",
                    3 to "марте",
                    4 to "апреле",
                    5 to "мае",
                    6 to "июне",
                    7 to "июле",
                    8 to "августе",
                    9 to "сентябре",
                    10 to "октябре",
                    11 to "ноябре",
                    12 to "декабре"
                )
                val month = months[e.x.toInt()]
                val sumMoney = e.yVals[h!!.stackIndex].toInt()
                val message =
                    "В $month ${if (sumMoney >= 0) "получено" else "потрачено"} ${abs(sumMoney)} P"
                mBinding?.descriptionChartCommon?.text = message
            }

        }

        override fun onNothingSelected() {}
    }

    private val xAxisFormatter: ValueFormatter = object : ValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val v = value - 1
            return if (v == v.toInt().toFloat()) {
                getListMonths().getOrNull(v.toInt()) ?: "5"
            } else "5"
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
        val binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = findNavController()
        mCommonBarChart = binding.commonChart
        mRestBarChart = binding.restChart
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        createCommonChart()
//        createRestChart()
        return binding.root
    }

    private fun render(state: StatisticsState) {
        updateCommonChart(state.entriesCommonChart)
    }

    fun getListMonths(): Array<String> {
        return arrayOf(
            "Янв",
            "Фев",
            "Мар",
            "Апр",
            "Май",
            "Июн",
            "Июл",
            "Авг",
            "Сен",
            "Окт",
            "Ноя",
            "Дек"
        )
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
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

        mCommonBarChart.width

        val xAxis = mCommonBarChart.xAxis
        xAxis.position = XAxisPosition.TOP_INSIDE
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 12f
        xAxis.granularity = 1f
        xAxis.valueFormatter = xAxisFormatter
//        mCommonBarChart.axisLeft.valueFormatter = yAxisFormatter

        val l: Legend = mCommonBarChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.formSize = 8f
        l.formToTextSpace = 4f
        l.xEntrySpace = 6f

    }

    private fun updateCommonChart(barEntries: List<AppBarEntry>) {
        if (barEntries.isNullOrEmpty()) {
            println("!barEntries.isNullOrEmpty() $barEntries")
            return
        }

//        val values = mutableListOf<BarEntry>()
//        for (i in 1..12) {
//            values.add(
//                BarEntry(
//                    i.toFloat(),
//                    floatArrayOf(
//                        -(1000..10000).random().toFloat(),
//                        (1000..10000).random().toFloat()
//                    )
//                )
//            )
//        }

        val values = barEntries.map {
            BarEntry(
                it.x,
                it.yVals
            )
        }
        Timber.d("Log bars: ${values.joinToString()}")
        val set = BarDataSet(values, "")
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
        mRestBarChart.setDrawValueAboveBar(true)
        mRestBarChart.description.isEnabled = false
        mRestBarChart.setDrawGridBackground(false)

        val xAxis: XAxis = mRestBarChart.xAxis
        xAxis.position = XAxisPosition.BOTH_SIDED
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 13f
        xAxis.granularity = 1f
        xAxis.valueFormatter = xAxisFormatter

        val left: YAxis = mRestBarChart.axisLeft
        left.setDrawLabels(false)
        left.setDrawAxisLine(false)
        left.setDrawGridLines(false)
        left.setDrawZeroLine(true)

        left.zeroLineColor = Color.GRAY
        left.zeroLineWidth = 0.7f
        mRestBarChart.axisRight.isEnabled = false
        mRestBarChart.legend.isEnabled = false

        val values = ArrayList<BarEntry>()
        val colors: MutableList<Int> = ArrayList()

        val green = Color.rgb(110, 190, 102)
        val red = Color.rgb(211, 74, 88)

        for (i in 1..12) {
            val y = (-100..100).random().toFloat()
            val entry = BarEntry(i.toFloat(), y)
            values.add(entry)
            if (y <= 0) colors.add(red) else colors.add(green)
        }

        val set = BarDataSet(values, "Values")
        set.colors = colors
        set.setValueTextColors(colors)

        val data = BarData(set)
        data.setValueTextSize(13f)

        mRestBarChart.data = data
        mRestBarChart.invalidate()
    }

}




