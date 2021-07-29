package com.example.balance

class trash {
    //fun getListEntries(): MutableList<BarEntry> {
    //        val entries: MutableList<BarEntry> = mutableListOf()
    //
    //        for (i in 1..12) {
    //            val floatArray = mutableListOf<Float>()
    //            for (n in 1..(1..10).random()) {
    //                floatArray.add((1..100).random().toFloat())
    //            }
    //            val stackedEntry = BarEntry(i.toFloat(), floatArray.toFloatArray(), i)
    //            entries.add(stackedEntry)
    //            floatArray.clear()
    //        }
    //        return entries
    //    }
    //    fun createProfitStackedBarChart() {
    //
    //        val chart = mBinding?.barChartProfits
    ////
    ////        val values1 = mutableListOf<BarEntry>()
    ////        val values2 = mutableListOf<BarEntry>()
    ////        val values3 = mutableListOf<BarEntry>()
    ////
    ////
    ////        for (i in 0 until 12) {
    ////            values1.add(
    ////                BarEntry(
    ////                    i.toFloat(),
    ////                    (1..60).random().toFloat(),
    ////                    (1..1000).random().toFloat(),
    ////                )
    ////            )
    ////            values2.add(
    ////                BarEntry(
    ////                    i.toFloat(),
    ////                    (1..60).random().toFloat(),
    ////                    (1..1000).random().toFloat(),
    ////                )
    ////            )
    ////            values3.add(
    ////                BarEntry(
    ////                    i.toFloat(),
    ////                    (1..60).random().toFloat(),
    ////                    (1..1000).random().toFloat(),
    ////                )
    ////            )
    ////        }
    ////
    ////        // create a dataset and give it a type
    ////        val set1 = BarDataSet(values1, "category 1")
    ////        set1.setColor(ColorTemplate.COLORFUL_COLORS[0], 130)
    ////        set1.setDrawValues(true)
    ////
    ////        val set2 = BarDataSet(values2, "category 2")
    ////        set2.setColor(ColorTemplate.COLORFUL_COLORS[1], 130)
    ////        set2.setDrawValues(true)
    ////
    ////        val set3 = BarDataSet(values3, "category 3")
    ////        set3.setColor(ColorTemplate.COLORFUL_COLORS[2], 130)
    ////        set3.setDrawValues(true)
    ////
    ////        val dataSets = mutableListOf<IBarDataSet>()
    ////        dataSets.add(set1)
    ////        dataSets.add(set2)
    ////        dataSets.add(set3)
    //
    //
    //        val entries = getListEntries()
    //
    //        val dataset = BarDataSet(entries, "Доходы")
    //
    //
    //        val colors = ArrayList<Int>()
    //
    //        for (i in 1..10) {
    //            colors.add(Color.rgb((1..255).random(), (1..255).random(), (1..255).random()))
    //        }
    //        dataset.colors = colors
    //
    //        val xAxisFormatter: ValueFormatter = object : ValueFormatter() {
    //
    //            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
    //                return getListMonths().getOrNull(value.toInt()) ?: value.toString()
    //            }
    //
    //        }
    //
    //        val yAxisFormatter: ValueFormatter = object : ValueFormatter() {
    //
    //            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
    //                return "${value.toInt()}Р"
    //            }
    //
    //        }
    //
    //        if (chart != null) {
    ////            val data = BarData(dataSet)
    ////            chart.data = data
    //
    //            val legend = chart.legend
    //
    ////            legend.setExtra(colors.toIntArray(), arrayOf("1","2","3","4","5","6","7","8","9","10","11","12"))
    //
    ////            chart.background = null
    //
    //            val xAxis: XAxis = chart.xAxis
    //            xAxis.valueFormatter = xAxisFormatter
    //            xAxis.position = XAxis.XAxisPosition.BOTTOM
    //
    //            val yAxis: YAxis = chart.axisLeft
    //            yAxis.valueFormatter = yAxisFormatter
    //
    //            chart.xAxis.setDrawGridLines(false)
    //            chart.axisLeft.setDrawGridLines(false)
    //            chart.axisRight.setDrawGridLines(false)
    //            chart.setBorderWidth(0f)
    //
    //
    //            val desc = Description()
    //            desc.text = "nj"
    //            chart.description = desc
    //        }
    //    }
    //    fun createCostsStackedBarChart() {
    //        val entries = getListEntries()
    //
    //        val dataset = BarDataSet(entries, "Расходы")
    //
    //        val chart = mBinding?.barChartCosts
    //
    //        val xAxisFormatter: ValueFormatter = object : ValueFormatter() {
    //
    //            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
    //                return getListMonths().getOrNull(value.toInt()) ?: value.toString()
    //            }
    //
    //        }
    //
    //        val yAxisFormatter: ValueFormatter = object : ValueFormatter() {
    //
    //            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
    //                return "${value}Р"
    //            }
    //
    //        }
    //
    //        if (chart != null) {
    //            val data = BarData(dataset)
    //            chart.data = data
    //            chart.setDrawBarShadow(true)
    //
    //            val xAxis: XAxis = chart.xAxis
    //            xAxis.valueFormatter = xAxisFormatter
    //            xAxis.position = XAxis.XAxisPosition.BOTTOM
    //
    //            val yAxis: YAxis = chart.axisLeft
    //            yAxis.valueFormatter = yAxisFormatter
    //
    //
    //            val desc = Description()
    //            desc.text = "nj"
    //            chart.description = desc
    //        }
    //    }
    //    fun createBubbleChart() {
    //
    ////        val chart = mBinding?.bubbleChart
    ////
    ////        val values1 = java.util.ArrayList<BubbleEntry>()
    ////        val values2 = java.util.ArrayList<BubbleEntry>()
    ////        val values3 = java.util.ArrayList<BubbleEntry>()
    ////
    ////        for (i in 0 until 12) {
    ////            values1.add(
    ////                BubbleEntry(
    ////                    i.toFloat(),
    ////                    (1..60).random().toFloat(),
    ////                    (1..1000).random().toFloat(),
    ////                )
    ////            )
    ////            values2.add(
    ////                BubbleEntry(
    ////                    i.toFloat(),
    ////                    (1..60).random().toFloat(),
    ////                    (1..1000).random().toFloat(),
    ////                )
    ////            )
    ////            values3.add(
    ////                BubbleEntry(
    ////                    i.toFloat(),
    ////                    (1..60).random().toFloat(),
    ////                    (1..1000).random().toFloat(),
    ////                )
    ////            )
    ////        }
    ////
    ////        val xAxisFormatter: ValueFormatter = object : ValueFormatter() {
    ////
    ////            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
    ////                return getListMonths().getOrNull(value.toInt()) ?: value.toString()
    ////            }
    ////
    ////        }
    ////
    ////        val yAxisFormatter: ValueFormatter = object : ValueFormatter() {
    ////
    ////            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
    ////                return "${value}Р"
    ////            }
    ////
    ////        }
    ////
    ////
    ////        // create a dataset and give it a type
    ////        val set1 = BubbleDataSet(values1, "category 1")
    ////        set1.setColor(ColorTemplate.COLORFUL_COLORS[0], 130)
    ////        set1.setDrawValues(true)
    ////
    ////        val set2 = BubbleDataSet(values2, "category 2")
    ////        set2.setColor(ColorTemplate.COLORFUL_COLORS[1], 130)
    ////        set2.setDrawValues(true)
    ////
    ////        val set3 = BubbleDataSet(values3, "category 3")
    ////        set3.setColor(ColorTemplate.COLORFUL_COLORS[2], 130)
    ////        set3.setDrawValues(true)
    ////
    ////        val dataSets = java.util.ArrayList<IBubbleDataSet>()
    ////        dataSets.add(set1) // add the data sets
    ////        dataSets.add(set2)
    ////        dataSets.add(set3)
    ////
    ////        // create a data object with the data sets
    ////
    ////        // create a data object with the data sets
    ////        val data = BubbleData(dataSets)
    ////        data.setDrawValues(false)
    ////        data.setValueTextSize(8f)
    ////        data.setValueTextColor(Color.WHITE)
    ////        data.setHighlightCircleWidth(1.5f)
    ////
    ////
    ////
    ////        if (chart != null) {
    ////            val xAxis: XAxis = chart.xAxis
    ////            xAxis.valueFormatter = xAxisFormatter
    ////            xAxis.position = XAxis.XAxisPosition.BOTTOM
    ////
    ////            val yAxis: YAxis = chart.axisLeft
    ////
    ////            chart.xAxis.setDrawGridLines(true)
    ////            chart.axisLeft.setDrawGridLines(false)
    ////            chart.axisRight.setDrawGridLines(false)
    ////
    ////            chart.data = data
    ////            chart.invalidate()
    ////        }
    //
    //    }
}