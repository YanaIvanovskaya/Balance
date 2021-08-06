package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.StatisticsAccessor
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class GeneralStatisticsState(
    val yearsOfUse: List<Int>,
    val entriesGeneralBarChart: List<BarEntry>,
    val entriesProfitLossBarChart: List<BarEntry>,
    val entriesCostsPieChart: List<PieEntry>,
    val entriesProfitPieChart: List<PieEntry>
) {

    companion object {
        fun default() = GeneralStatisticsState(
            yearsOfUse = listOf(),
            entriesGeneralBarChart = mutableListOf(),
            entriesProfitLossBarChart = mutableListOf(),
            entriesCostsPieChart = mutableListOf(),
            entriesProfitPieChart = mutableListOf()
        )
    }

}

class GeneralStatisticsViewModel(
    val recordRepository: RecordRepository
) : ViewModel() {

    val state = MutableLiveData(GeneralStatisticsState.default())

    init {
        viewModelScope.launch {
            val yearsOfUse = StatisticsAccessor.getListYearsOfUse()

            state.value = state.value?.copy(
                yearsOfUse = yearsOfUse
            )

            val entriesGeneralBarChart = getEntriesForChart(ChartType.COMMON_CHART)
            val entriesProfitLossBarChart = getEntriesForChart(ChartType.REST_CHART)
            val entriesCostsPieChart = getEntriesForPieChart(CategoryType.CATEGORY_COSTS)
            val entriesProfitPieChart = getEntriesForPieChart(CategoryType.CATEGORY_PROFIT)

            state.value = state.value?.copy(
                entriesGeneralBarChart = entriesGeneralBarChart,
                entriesProfitLossBarChart = entriesProfitLossBarChart,
                entriesCostsPieChart = entriesCostsPieChart,
                entriesProfitPieChart = entriesProfitPieChart
            )
        }
    }

    enum class ChartType {
        COMMON_CHART,
        REST_CHART
    }

    private suspend fun getEntriesForPieChart(categoryType: CategoryType): List<PieEntry> {
        return withContext(Dispatchers.IO) {
            val pieEntries = mutableListOf<PieEntry>()
            for (pair in StatisticsAccessor.getListCategoryWithSum(categoryType)) {
                val value = pair.second.toFloat()
                if (value != 0f) {
                    val pieEntry = PieEntry(value, pair.first)
                    pieEntries.add(pieEntry)
                }
            }
            pieEntries
        }
    }

    private suspend fun getEntriesForChart(chartType: ChartType): List<BarEntry> {
        return withContext(Dispatchers.IO) {
            val entries = mutableListOf<BarEntry>()
            val yearsOfUse = state.value?.yearsOfUse
            if (!yearsOfUse.isNullOrEmpty()) {

                var counter = 1
                for (year in yearsOfUse) {
                    val months = StatisticsAccessor.getListMonthsInYear(year)
                    println(months)
                    for (month in months) {
                        val profitValue = StatisticsAccessor.getMonthlySumByRecordType(
                            recordType = RecordType.PROFITS,
                            month = month,
                            year = year
                        )
                        val costsValue = StatisticsAccessor.getMonthlySumByRecordType(
                            recordType = RecordType.COSTS,
                            month = month,
                            year = year
                        )

                        if (profitValue != 0 || costsValue != 0) {
                            val barEntry = when (chartType) {
                                ChartType.COMMON_CHART -> {
                                    BarEntry(
                                        counter.toFloat(),
                                        floatArrayOf(
                                            costsValue.toFloat() * -1,
                                            profitValue.toFloat()
                                        ),
                                        month
                                    )
                                }
                                ChartType.REST_CHART -> {
                                    BarEntry(
                                        counter.toFloat(),
                                        (profitValue - costsValue).toFloat(),
                                        month
                                    )
                                }
                            }
                            entries.add(barEntry)
                        }
                        counter++
                    }
                }
            }
            entries
        }
    }

}