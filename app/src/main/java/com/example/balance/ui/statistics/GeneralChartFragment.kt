package com.example.balance.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.BalanceApp
import com.example.balance.Case
import com.example.balance.R
import com.example.balance.data.StatisticsAccessor
import com.example.balance.data.record.RecordType
import com.example.balance.databinding.FragmentGeneralChartBinding
import com.example.balance.getMonthName
import com.example.balance.presentation.GeneralStatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.ui.recycler_view.item.Item
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs


data class GeneralChartState(
    val selectedBar: Int,
    val entries: List<BarEntry>
) {

    companion object {
        fun default() = GeneralChartState(
            selectedBar = 0,
            entries = listOf()
        )
    }

}

class GeneralChartViewModel : ViewModel() {

    val state = MutableLiveData(GeneralChartState.default())

    init {
        viewModelScope.launch {
            val entries = getEntries()
            state.value = state.value?.copy(
                entries = entries
            )
        }
    }

    private suspend fun getEntries(): List<BarEntry> {
        return withContext(Dispatchers.IO) {
            val entries = mutableListOf<BarEntry>()
            val yearsOfUse = StatisticsAccessor.getListYearsOfUse()

            if (!yearsOfUse.isNullOrEmpty()) {
                var counter = 1

                yearsOfUse.forEach { year ->
                    val months = StatisticsAccessor.getListMonthsInYear(year)
                    months.forEach { month ->
                        val profitValue = StatisticsAccessor.getMonthlySumByRecordType(
                            recordType = RecordType.PROFITS,
                            month = month,
                            year = year
                        )
                        val costsValue = StatisticsAccessor.getMonthlySumByRecordType(
                            recordType = RecordType.COSTS,
                            month = month,
                            year = year
                        )
                        if (profitValue != 0 || costsValue != 0) {
                            entries.add(
                                BarEntry(
                                    counter.toFloat(),
                                    floatArrayOf(
                                        costsValue.toFloat() * -1,
                                        profitValue.toFloat()
                                    ),
                                    month
                                )
                            )
                        }
                        counter++
                    }
                }
            }
            entries
        }
    }

}

class GeneralChartFragment() :
    Fragment(R.layout.fragment_general_chart) {

    private lateinit var mGeneralBarChart: BarChart
    private var mBinding: FragmentGeneralChartBinding? = null
    private val mViewModel by getViewModel {
        GeneralChartViewModel()
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
            updateGeneralBarChart(it.entries)
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