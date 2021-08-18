package com.example.balance.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.balance.R
import com.example.balance.databinding.FragmentProfitPieChartBinding
import com.example.balance.presentation.getViewModel
import com.example.balance.presentation.statistics.ProfitPieChartViewModel
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
        ProfitPieChartViewModel()
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
            val entries = it.entries
            mBinding?.resumeProfitPieChart?.text =
                if (entries.isNotEmpty())
                    "Больше всего получено по категории \"${it.maxProfitCategory}\""
                else ""
            updateProfitPieChart(entries)
            mBinding?.preloaderProfitPieChart?.isVisible = !it.isContentLoaded
        })
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun createProfitPieChart() {
        mProfitPieChart.apply {
            setUsePercentValues(true)
            setExtraOffsets(5f, 10f, 5f, 5f)
            setHoleColor(Color.TRANSPARENT)
            setNoDataText("Пока тут ничего нет")
            setNoDataTextColor(ResourcesCompat.getColor(resources, R.color.grey_800, null))
            setDrawEntryLabels(false)
            description.isEnabled = false
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true
            holeRadius = 25f
            transparentCircleRadius = 0f
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400, EaseInOutQuad)

        }
        mProfitPieChart.legend.apply {
            setDrawInside(false)
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            isWordWrapEnabled = true
            xEntrySpace = 7f
            textSize = 14f
            textColor = ResourcesCompat.getColor(resources, R.color.grey_800, null)
            yEntrySpace = 2f
            yOffset = 10f
        }
    }

    private fun updateProfitPieChart(pieEntries: List<PieEntry>) {
        if (pieEntries.isNullOrEmpty()) {
            return
        }
        val colors = mutableListOf<Int>()
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        val dataSet = PieDataSet(pieEntries, "").apply {
            setDrawIcons(false)
            sliceSpace = 3f
            iconsOffset = MPPointF(0f, 40f)
            selectionShift = 5f
            this.colors = colors.shuffled()
        }
        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(12f)
            setValueTextColor(Color.BLACK)
        }
        mProfitPieChart.data = data
        mProfitPieChart.invalidate()
    }
}
