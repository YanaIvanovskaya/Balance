package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.StatisticsAccessor
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.toUpperFirst
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class GeneralStatisticsState(
    val yearsOfUse: List<Int>,
    val maxProfitCategory: String,
    val maxCostsCategory: String,
    val entriesGeneralBarChart: List<BarEntry>,
    val entriesProfitLossBarChart: List<BarEntry>,
    val entriesCostsPieChart: List<PieEntry>,
    val entriesProfitPieChart: List<PieEntry>,
    val haveCosts: Boolean,
    val haveProfits: Boolean
) {

    companion object {
        fun default() = GeneralStatisticsState(
            yearsOfUse = listOf(),
            entriesGeneralBarChart = mutableListOf(),
            entriesProfitLossBarChart = mutableListOf(),
            entriesCostsPieChart = mutableListOf(),
            entriesProfitPieChart = mutableListOf(),
            maxProfitCategory = "",
            maxCostsCategory = "",
            haveCosts = true,
            haveProfits = true
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
            if (yearsOfUse.isNotEmpty()) {
                val haveCosts =
                    withContext(Dispatchers.IO) {
                        recordRepository.getRecordsByType(RecordType.COSTS).first()
                    }.isNotEmpty()
                val haveProfits =
                    withContext(Dispatchers.IO) {
                        recordRepository.getRecordsByType(RecordType.PROFITS).first()
                    }.isNotEmpty()

                state.value = state.value?.copy(
                    yearsOfUse = yearsOfUse,
                    haveCosts = haveCosts,
                    haveProfits = haveProfits
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
            } else {
                state.value = state.value?.copy(
                    haveCosts = false,
                    haveProfits = false
                )
            }

        }
    }

    enum class ChartType {
        COMMON_CHART,
        REST_CHART
    }

    private suspend fun getEntriesForPieChart(categoryType: CategoryType): List<PieEntry> {
        return withContext(Dispatchers.IO) {
            val pieEntries = mutableListOf<PieEntry>()
            val listCategoryWithSum = StatisticsAccessor.getListCategoryWithSum(categoryType)
            val maxSumCategory: Pair<String, Int>? = listCategoryWithSum.maxByOrNull { it.second }
            withContext(Dispatchers.Main) {
                if (maxSumCategory != null) {
                    state.value = when (categoryType) {
                        CategoryType.CATEGORY_COSTS -> state.value?.copy(maxCostsCategory = maxSumCategory.first)
                        else -> state.value?.copy(maxProfitCategory = maxSumCategory.first)
                    }
                }
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