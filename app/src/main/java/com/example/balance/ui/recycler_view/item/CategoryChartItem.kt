package com.example.balance.ui.recycler_view.item

import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryType
import com.example.balance.formatAsSum
import com.example.balance.toUpperFirst
import com.example.balance.ui.recycler_view.ViewHolderFactory
import com.example.balance.ui.statistics.BarValueFormatter
import com.example.balance.ui.statistics.CustomXAxisRenderer
import com.example.balance.ui.statistics.RoundedBarChartRenderer
import com.example.balance.ui.statistics.XAxisFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.util.Collections.max

class CategoryChartItem(
    val category: Category,
    val sumAverageMonth: Int,
    val barEntries: List<BarEntry>
) : Item {

    override fun getItemViewType(): Int = Item.CATEGORY_CHART_ITEM_TYPE

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val statisticsViewHolder = viewHolder as ViewHolderFactory.CategoryChartViewHolder

        val colors: MutableList<Int> = ArrayList()
        val resources = viewHolder.itemView.resources
        val green200 = ResourcesCompat.getColor(resources, R.color.green_200, null)
        val red200 = ResourcesCompat.getColor(resources, R.color.red_200, null)
        val green300 = ResourcesCompat.getColor(resources, R.color.green_300, null)
        val red300 = ResourcesCompat.getColor(resources, R.color.red_300, null)

        statisticsViewHolder.apply {
            val prefix = when (category.type) {
                CategoryType.CATEGORY_COSTS -> {
                    sumAvgMonthly.setTextColor(red300)
                    "- "
                }
                else -> {
                    sumAvgMonthly.setTextColor(green300)
                    "+ "
                }
            }
            val sumAvgMonthly = "$prefix${sumAverageMonth.formatAsSum()}"
            nameCategoryText.text = category.name.toUpperFirst()
            this.sumAvgMonthly.text = sumAvgMonthly
        }

        val chart = statisticsViewHolder.categoryChart.apply {
            setExtraOffsets(5f, 20f, 5f, 10f)
            setVisibleXRangeMaximum(6f)
            setDrawValueAboveBar(true)
            setDrawGridBackground(false)
            setFitBars(true)
            animateX(500)
            animateY(700)
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            axisLeft.axisMinimum = 0f
        }
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.TOP
            textSize = 11f
            yOffset = 40f
            granularity = 1f
            valueFormatter = XAxisFormatter(chart)
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }
        chart.setXAxisRenderer(
            CustomXAxisRenderer(
                chart.viewPortHandler,
                chart.xAxis,
                chart.getTransformer(AxisDependency.LEFT)
            )
        )
        chart.renderer =
            RoundedBarChartRenderer(
                chart,
                chart.animator,
                chart.viewPortHandler
            ).apply { setRadius(20) }
        barEntries.forEach { _ ->
            when (category.type) {
                CategoryType.CATEGORY_COSTS -> colors.add(red200)
                else -> colors.add(green200)
            }
        }
        val set = BarDataSet(barEntries, "").apply {
            this.colors = colors
            valueFormatter = BarValueFormatter()
        }
        chart.apply {
            data = BarData(set).apply { setValueTextSize(12f) }
            setVisibleXRangeMaximum(6f)
            moveViewToX(max(barEntries.map { it.x }))
        }
    }

}