package com.example.balance.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentCostsPieChartBinding
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


class CostsPieChartFragment : Fragment(R.layout.fragment_costs_pie_chart) {

    private lateinit var mCostsPieChart: PieChart
    private var mBinding: FragmentCostsPieChartBinding? = null
    private val mGeneralViewModel by getViewModel {
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
        mGeneralViewModel.state.observe(viewLifecycleOwner, {
            mBinding?.resumeCostsPieChart?.text =
                if (it.haveCosts) "Больше всего потрачено на категорию \"${it.maxCostsCategory}\""
                else ""
            val entries = it.entriesCostsPieChart
            updateCostsPieChart(entries)
            if (entries.isNotEmpty() || !it.haveCosts)
                mBinding?.preloaderCostsPieChart?.visibility = View.GONE
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
        mCostsPieChart.isDrawHoleEnabled = true
        mCostsPieChart.setHoleColor(Color.TRANSPARENT)
        mCostsPieChart.holeRadius = 25f
        mCostsPieChart.transparentCircleRadius = 0f
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
        l.yOffset = 5f

        mCostsPieChart.setDrawEntryLabels(false)
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

        val colors = mutableListOf<Int>()
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors.shuffled()

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        mCostsPieChart.data = data
        mCostsPieChart.invalidate()
    }
}
