package com.example.balance.ui.recycler_view.item

import android.graphics.Color
import android.graphics.RectF
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.Case
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryType
import com.example.balance.getMonthName
import com.example.balance.ui.recycler_view.ViewHolderFactory
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import kotlin.math.abs

class StatisticsItem(
    val category: Category,
    val sumAverageMonth: Int,
    val sumAverageCheque: Int,
    val barEntries: List<BarEntry>
) : Item {

    override fun getItemViewType(): Int {
        return Item.STATISTICS_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val statisticsViewHolder = viewHolder as ViewHolderFactory.StatisticsViewHolder

        statisticsViewHolder.nameCategoryText.text = category.name
        statisticsViewHolder.resumeAverageMonth.text = "$sumAverageMonth руб./мес."
        statisticsViewHolder.resumeAverageCheque.text = "$sumAverageCheque руб."

        val chart = statisticsViewHolder.categoryChart

        val xAxisFormatter: ValueFormatter = object : ValueFormatter() {

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val currentEntry = chart.data.dataSets[0].getEntriesForXValue(value)[0]
                return getMonthName(currentEntry.data as Int, Case.SHORT)
            }

        }

        val yAxisFormatter: ValueFormatter = object : ValueFormatter() {

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return if (abs(value) < 1000f) {
                    "${value.toInt()}"
                } else "${(value / 1000).toInt()}K"
            }

            override fun getFormattedValue(value: Float): String {
                return getAxisLabel(value, null)
            }

        }

        val onRestChartValueSelectedListener = object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val onValueSelectedRectF = RectF()
                if (e == null) return
                val bounds: RectF = onValueSelectedRectF
                chart.getBarBounds(e as BarEntry?, bounds)
                val position: MPPointF = chart.getPosition(e, AxisDependency.LEFT)
                MPPointF.recycleInstance(position)
            }

            override fun onNothingSelected() {}
        }

        chart.setOnChartValueSelectedListener(onRestChartValueSelectedListener)
        chart.setDrawValueAboveBar(true)
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setFitBars(true)
        chart.animateX(500)
        chart.animateY(700)

        chart.axisRight.isEnabled = false
        chart.axisLeft.setDrawGridLines(false)
        chart.axisLeft.setDrawZeroLine(true)
        chart.axisLeft.setLabelCount(7, false)
        chart.axisLeft.textSize = 12f
        chart.axisLeft.valueFormatter = yAxisFormatter

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 12f
        xAxis.granularity = 1f
        xAxis.valueFormatter = xAxisFormatter

        chart.legend.isEnabled = false

        val colors: MutableList<Int> = ArrayList()
        val green = Color.rgb(110, 190, 102)
        val red = Color.rgb(211, 74, 88)

        barEntries.forEach { _ ->
            when (category.type) {
                CategoryType.CATEGORY_COSTS -> colors.add(red)
                else -> colors.add(green)
            }
        }

        val set = BarDataSet(barEntries, "")
        set.colors = colors
        set.setValueTextColors(colors)

        val data = BarData(set)
        data.setValueTextSize(13f)
        chart.data = data
        chart.invalidate()

    }
}