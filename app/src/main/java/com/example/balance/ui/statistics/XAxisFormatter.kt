package com.example.balance.ui.statistics

import com.example.balance.Case
import com.example.balance.getMonthName
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class XAxisFormatter(
    private val chart: BarChart
) : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val currentEntry = chart.data.dataSets[0].getEntriesForXValue(value)[0]
        val data = currentEntry.data
        return if (data is List<*>) {
            val monthNumber = data[0] as Int
            val month = getMonthName(monthNumber, Case.SHORT).uppercase()
            val year = data[1].toString()
            if (monthNumber == 1 || monthNumber == 7) {
                "$year\n$month"
            } else {
                "\n$month"
            }
        } else ""
    }

}

//class YAxisFormatter : ValueFormatter() {
//
//    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//        return when (abs(value)) {
//            in 0f..999f -> {
//                if (value.mod(10f) == 0.0f) {
//                    "${value.toInt()}"
//                } else ""
//            }
//            else -> {
//                if (value.mod(1000f) == 0.0f) {
//                    "${(value / 1000).toInt()}K"
//                } else if (value.mod(100f) == 0.0f) {
//                    "${value / 1000}K"
//                } else ""
//            }
//        }
//    }
//
//}