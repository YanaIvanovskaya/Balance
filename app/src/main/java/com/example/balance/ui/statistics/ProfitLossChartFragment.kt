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
import com.example.balance.databinding.FragmentProfitLossChartBinding
import com.example.balance.getMonthName
import com.example.balance.presentation.GeneralStatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.github.mikephil.charting.charts.BarChart
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

data class ProfitLossChartState(
    val selectedBar: Int,
    val entries: List<BarEntry>
) {

    companion object {
        fun default() = ProfitLossChartState(
            selectedBar = 0,
            entries = listOf()
        )
    }

}

class ProfitLossChartViewModel : ViewModel() {

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
                                    (profitValue - costsValue).toFloat(),
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


class ProfitLossChartFragment : Fragment(R.layout.fragment_profit_loss_chart) {

    private lateinit var mProfitLossBarChart: BarChart
    private var mBinding: FragmentProfitLossChartBinding? = null
    private val mViewModel by getViewModel {
        ProfitLossChartViewModel()
    }
    private val onValueSelectedListener = object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e is BarEntry) {
                val month = getMonthName(e.data as Int, Case.NONE).uppercase()
                val sumMoney = e.y.toInt()
                val message = "$month  $sumMoney P"
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
//            if (entries.isNotEmpty() || (!it.haveCosts && !it.haveProfits))
//                mBinding?.preloaderProfitLossChart?.visibility = View.GONE
        })
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun createProfitLossBarChart() {
        mProfitLossBarChart.setOnChartValueSelectedListener(onValueSelectedListener)
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