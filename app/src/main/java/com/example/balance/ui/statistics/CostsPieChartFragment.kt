package com.example.balance.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.utils.Easing
import androidx.fragment.app.Fragment
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentCostsPieChartBinding
import com.example.balance.presentation.GeneralStatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.github.mikephil.charting.animation.Easing.EaseInOutQuad
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF

class CostsPieChartFragment : Fragment(R.layout.fragment_costs_pie_chart) {

    private lateinit var mCostsPieChart: PieChart
    private var mBinding: FragmentCostsPieChartBinding? = null
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
        val binding = FragmentCostsPieChartBinding.inflate(inflater, container, false)
        mBinding = binding
        mCostsPieChart = binding.costsPieChart
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createCostsPieChart()
        mViewModel.state.observe(viewLifecycleOwner, {
            updateCostsPieChart(it.entriesCostsPieChart)
        })
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun createCostsPieChart() {
        mCostsPieChart.setUsePercentValues(true)
        mCostsPieChart.description.isEnabled = false
        mCostsPieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        mCostsPieChart.dragDecelerationFrictionCoef = 0.95f
        mCostsPieChart.centerText = "Расходы"
        mCostsPieChart.isDrawHoleEnabled = true
        mCostsPieChart.setHoleColor(Color.WHITE)
        mCostsPieChart.setTransparentCircleColor(Color.WHITE)
        mCostsPieChart.setTransparentCircleAlpha(110)
        mCostsPieChart.holeRadius = 58f
        mCostsPieChart.transparentCircleRadius = 61f
        mCostsPieChart.setDrawCenterText(true)
        mCostsPieChart.rotationAngle = 0f
        mCostsPieChart.isRotationEnabled = true
        mCostsPieChart.isHighlightPerTapEnabled = true
        mCostsPieChart.animateY(1400, EaseInOutQuad)

        val l: Legend = mCostsPieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(true)
        l.isWordWrapEnabled = true
        l.xEntrySpace = 7f
        l.textSize = 12f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        mCostsPieChart.setEntryLabelColor(Color.WHITE)
        mCostsPieChart.setEntryLabelTextSize(12f)
    }

    private fun updateCostsPieChart(pieEntries: List<PieEntry>) {
        if (pieEntries.isNullOrEmpty()) {
            return
        }

        val dataSet = PieDataSet(pieEntries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors = java.util.ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        mCostsPieChart.data = data
        mCostsPieChart.highlightValues(null)
        mCostsPieChart.invalidate()
    }
}