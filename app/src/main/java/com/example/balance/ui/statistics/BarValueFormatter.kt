package com.example.balance.ui.statistics

import com.example.balance.roundTo
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.abs
import kotlin.math.roundToInt

class BarValueFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return when (abs(value).roundToInt().toString().length) {
            in 1..3 -> "${value.roundToInt()}"
            in 4..6 -> "${(value / 1000.0).roundTo(2)}K"
            in 7..9 -> "${(value / 1000000.0).roundTo(2)}M"
            else -> ""
        }
    }

}