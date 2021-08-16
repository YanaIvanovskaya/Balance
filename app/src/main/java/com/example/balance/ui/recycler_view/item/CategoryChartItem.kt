package com.example.balance.ui.recycler_view.item

import android.graphics.Color
import android.graphics.RectF
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.Case
import com.example.balance.R
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryType
import com.example.balance.getMonthName
import com.example.balance.toUpperFirst
import com.example.balance.ui.recycler_view.ViewHolderFactory
import com.example.balance.ui.statistics.*
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class CategoryChartItem(
    val category: Category,
    val sumAverageMonth: Int,
    val barEntries: List<BarEntry>
) : Item {

    override fun getItemViewType(): Int {
        return Item.CATEGORY_CHART_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val statisticsViewHolder = viewHolder as ViewHolderFactory.CategoryChartViewHolder
        statisticsViewHolder.nameCategoryText.text = category.name.toUpperFirst()

        val colors: MutableList<Int> = ArrayList()
        val resources = viewHolder.itemView.resources

        val green200 = ResourcesCompat.getColor(resources, R.color.green_200, null)
        val red200 = ResourcesCompat.getColor(resources, R.color.red_200, null)
        val green300 = ResourcesCompat.getColor(resources, R.color.green_300, null)
        val red300 = ResourcesCompat.getColor(resources, R.color.red_300, null)

        val sign = when (category.type) {
            CategoryType.CATEGORY_COSTS -> {
                statisticsViewHolder.sumAvgMonthly.setTextColor(red300)
                "- "
            }
            else -> {
                statisticsViewHolder.sumAvgMonthly.setTextColor(green300)
                "+ "
            }
        }
        val sumAvgMonthly = "$sign$sumAverageMonth"
        statisticsViewHolder.sumAvgMonthly.text = sumAvgMonthly

        val chart = statisticsViewHolder.categoryChart
        chart.setExtraOffsets(5f, 20f, 5f, 10f)
        chart.setDrawValueAboveBar(true)
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setFitBars(true)
        chart.animateX(500)
        chart.animateY(700)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 11f
        xAxis.yOffset = 40f
        xAxis.granularity = 1f
        xAxis.valueFormatter = XAxisFormatter(chart)

        chart.setXAxisRenderer(
            CustomXAxisRenderer(
                chart.viewPortHandler,
                chart.xAxis,
                chart.getTransformer(AxisDependency.LEFT)
            )
        )

        chart.legend.isEnabled = false

        val barChartRender =
            CustomBarChartRender(chart, chart.animator, chart.viewPortHandler)
        barChartRender.setRadius(20)
        chart.renderer = barChartRender

        chart.axisRight.isEnabled = false
        chart.axisLeft.isEnabled = false
        chart.axisLeft.axisMinimum = 0f

        barEntries.forEach { _ ->
            when (category.type) {
                CategoryType.CATEGORY_COSTS -> colors.add(red200)
                else -> colors.add(green200)
            }
        }

        val set = BarDataSet(barEntries, "")
        set.colors = colors
        set.valueFormatter = BarValueFormatter()

        val data = BarData(set)
        data.setValueTextSize(12f)
        chart.setVisibleXRangeMaximum(6f)
        chart.data = data
        chart.invalidate()
    }
}