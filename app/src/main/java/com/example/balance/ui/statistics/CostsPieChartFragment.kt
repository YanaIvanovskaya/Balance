package com.example.balance.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.data.StatisticsAccessor
import com.example.balance.data.category.CategoryType
import com.example.balance.databinding.FragmentCostsPieChartBinding
import com.example.balance.presentation.GeneralStatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.toUpperFirst
import com.github.mikephil.charting.animation.Easing.EaseInOutQuad
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CostsPieChartState(
    val selectedBar: Int,
    val entries: List<PieEntry>,
    val maxCostsCategory: String
) {

    companion object {
        fun default() = CostsPieChartState(
            selectedBar = 0,
            entries = listOf(),
            maxCostsCategory = ""
        )
    }

}

class CostsPieChartViewModel : ViewModel() {

    val state = MutableLiveData(CostsPieChartState.default())

    init {
        viewModelScope.launch {
            val entries = getEntries()
            state.value = state.value?.copy(
                entries = entries
            )
        }
    }

    private suspend fun getEntries(): List<PieEntry> {
        return withContext(Dispatchers.IO) {
            val pieEntries = mutableListOf<PieEntry>()
            val listCategoryWithSum =
                StatisticsAccessor.getListCategoryWithSum(CategoryType.CATEGORY_COSTS)
            val maxSumCategory: Pair<String, Int>? = listCategoryWithSum.maxByOrNull { it.second }
            withContext(Dispatchers.Main) {
                state.value = state.value?.copy(maxCostsCategory = maxSumCategory?.first ?: "")
            }
            for (pair in listCategoryWithSum) {
                val value = pair.second.toFloat()
                if (value != 0f) {
                    val pieEntry = PieEntry(value, pair.first.toUpperFirst())
                    pieEntries.add(pieEntry)
                }
            }
            pieEntries
        }
    }

}


class CostsPieChartFragment : Fragment(R.layout.fragment_costs_pie_chart) {

    private lateinit var mCostsPieChart: PieChart
    private var mBinding: FragmentCostsPieChartBinding? = null
    private val mViewModel by getViewModel {
        CostsPieChartViewModel()
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
            val entries = it.entries
            mBinding?.resumeCostsPieChart?.text =
                if (entries.isNotEmpty()) "Больше всего потрачено на категорию \"${it.maxCostsCategory}\""
                else ""
            updateCostsPieChart(entries)
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
        mCostsPieChart.setNoDataText("Пока тут ничего нет")
        mCostsPieChart.setNoDataTextColor(ResourcesCompat.getColor(resources,R.color.grey_800,null))

        val l: Legend = mCostsPieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.isWordWrapEnabled = true
        l.xEntrySpace = 7f
        l.textSize = 14f
        l.textColor = ResourcesCompat.getColor(resources,R.color.grey_800,null)
        l.yEntrySpace = 2f
        l.yOffset = 10f
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
