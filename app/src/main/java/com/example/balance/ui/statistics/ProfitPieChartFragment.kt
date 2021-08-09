package com.example.balance.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentProfitPieChartBinding
import com.example.balance.presentation.GeneralStatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.github.mikephil.charting.animation.Easing.EaseInOutQuad
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF

class ProfitPieChartFragment : Fragment(R.layout.fragment_profit_pie_chart) {

    private lateinit var mProfitPieChart: PieChart
    private var mBinding: FragmentProfitPieChartBinding? = null
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
        val binding = FragmentProfitPieChartBinding.inflate(inflater, container, false)
        mBinding = binding
        mProfitPieChart = binding.profitPieChart
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createProfitPieChart()
        mViewModel.state.observe(viewLifecycleOwner, {
            mBinding?.resumeProfitPieChart?.text =
                if (it.haveProfits) "Больше всего получено по категории \"${it.maxProfitCategory}\""
                else ""
            val entries = it.entriesProfitPieChart
            updateProfitPieChart(entries)
            if (entries.isNotEmpty())
                mBinding?.preloaderProfitPieChart?.visibility = View.GONE
        })
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun createProfitPieChart() {
        mProfitPieChart.setUsePercentValues(true)
        mProfitPieChart.description.isEnabled = false
        mProfitPieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        mProfitPieChart.dragDecelerationFrictionCoef = 0.95f
        mProfitPieChart.isDrawHoleEnabled = true
        mProfitPieChart.setHoleColor(Color.TRANSPARENT)
        mProfitPieChart.holeRadius = 25f
        mProfitPieChart.transparentCircleRadius = 0f
        mProfitPieChart.rotationAngle = 0f
        mProfitPieChart.isRotationEnabled = true
        mProfitPieChart.isHighlightPerTapEnabled = true
        mProfitPieChart.animateY(1400, EaseInOutQuad)

        val l: Legend = mProfitPieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(true)
        l.isWordWrapEnabled = true
        l.xEntrySpace = 7f
        l.textSize = 12f
        l.yEntrySpace = 0f
        l.yOffset = 5f

        mProfitPieChart.setDrawEntryLabels(false)
    }

    private fun updateProfitPieChart(pieEntries: List<PieEntry>) {
        if (pieEntries.isNullOrEmpty()) {
            return
        }

        val dataSet = PieDataSet(pieEntries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors = mutableListOf<Int>()
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors.shuffled()

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        mProfitPieChart.data = data
        mProfitPieChart.invalidate()
    }
}
