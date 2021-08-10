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
import com.example.balance.R
import com.example.balance.data.StatisticsAccessor
import com.example.balance.data.category.CategoryType
import com.example.balance.databinding.FragmentProfitPieChartBinding
import com.example.balance.presentation.GeneralStatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.toUpperFirst
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ProfitPieChartState(
    val selectedBar: Int,
    val entries: List<PieEntry>,
    val maxProfitCategory: String
) {

    companion object {
        fun default() = ProfitPieChartState(
            selectedBar = 0,
            entries = listOf(),
            maxProfitCategory = ""
        )
    }

}

class ProfitPieChartViewModel : ViewModel() {

    val state = MutableLiveData(ProfitPieChartState.default())

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
                StatisticsAccessor.getListCategoryWithSum(CategoryType.CATEGORY_PROFIT)
            val maxSumCategory: Pair<String, Int>? = listCategoryWithSum.maxByOrNull { it.second }
            withContext(Dispatchers.Main) {
                state.value = state.value?.copy(maxProfitCategory = maxSumCategory?.first ?: "")
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
                if (entries.isNotEmpty()) "Больше всего получено по категории \"${it.maxProfitCategory}\""
                else ""
            updateProfitPieChart(entries)
//            if (entries.isNotEmpty())
//                mBinding?.preloaderProfitPieChart?.visibility = View.GONE
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
