package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.ui.recycler_view.item.StatisticsItem
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class StatisticsState(
    val yearsOfUse: List<Int>,
    val entriesCommonChart: List<BarEntry>,
    val entriesRestChart: List<BarEntry>,
    val profitStatItems: List<StatisticsItem>,
    val costsStatItems: List<StatisticsItem>,
) {

    companion object {
        fun default() = StatisticsState(
            yearsOfUse = listOf(),
            entriesCommonChart = mutableListOf(),
            entriesRestChart = mutableListOf(),
            profitStatItems = mutableListOf(),
            costsStatItems = mutableListOf()
        )
    }

}

class StatisticsViewModel(
    val recordRepository: RecordRepository,
    val categoryRepository: CategoryRepository
) : ViewModel() {

    val state = MutableLiveData(StatisticsState.default())

    init {
        viewModelScope.launch {
            val yearsOfUse =
                withContext(Dispatchers.IO) { recordRepository.getYearsOfUse().first() }

            state.value = state.value?.copy(yearsOfUse = yearsOfUse)

            val entriesCommonChart = getEntriesForStaticChart(ChartType.GENERAL_CHART)
            val entriesRestChart = getEntriesForStaticChart(ChartType.PROFIT_LOSS_CHART)
            val profitStatItems = getStatisticsItems(CategoryType.CATEGORY_PROFIT)
            val costsStatItems = getStatisticsItems(CategoryType.CATEGORY_COSTS)

            state.value = state.value?.copy(
                entriesCommonChart = entriesCommonChart,
                entriesRestChart = entriesRestChart,
                profitStatItems = profitStatItems,
                costsStatItems = costsStatItems
            )

        }
    }

    fun getProfitStatItems(): List<StatisticsItem> = state.value?.profitStatItems ?: listOf()

    fun getCostsStatItems(): List<StatisticsItem> = state.value?.costsStatItems ?: listOf()

    enum class ChartType {
        GENERAL_CHART,
        PROFIT_LOSS_CHART
    }

    private suspend fun getEntriesForCategoryChart(
        category: Category
    ): List<BarEntry> {
        return withContext(Dispatchers.IO) {
            val entries = mutableListOf<BarEntry>()
            val yearsOfUse = state.value?.yearsOfUse
            if (!yearsOfUse.isNullOrEmpty()) {
                var counter = 1
                for (year in yearsOfUse) {
                    val months = recordRepository.getMonthsInYear(year).first()
                    for (month in months) {
                        val value = recordRepository.getMonthlyAmount(
                            categoryId = category.id,
                            month = month,
                            year = year
                        ).first() ?: 0

                        if (value != 0) {
                            val barEntry = BarEntry(
                                counter.toFloat(),
                                value.toFloat(),
                                month
                            )
                            entries.add(barEntry)
                        }
                        counter++
                    }
                }
            }
            entries
        }
    }

    private suspend fun getEntriesForStaticChart(
        chartType: ChartType
    ): List<BarEntry> {
        return withContext(Dispatchers.IO) {
            val entries = mutableListOf<BarEntry>()
            val yearsOfUse = state.value?.yearsOfUse
            if (!yearsOfUse.isNullOrEmpty()) {

                var counter = 1
                for (year in yearsOfUse) {
                    val months = recordRepository.getMonthsInYear(year).first()
                    for (month in months) {
                        val profitValue = recordRepository.getMonthlyAmount(
                            recordType = RecordType.PROFITS,
                            month = month,
                            year = year
                        ).first() ?: 0
                        val costsValue = recordRepository.getMonthlyAmount(
                            recordType = RecordType.COSTS,
                            month = month,
                            year = year
                        ).first() ?: 0

                        if (profitValue != 0 || costsValue != 0) {
                            val barEntry = when (chartType) {
                                ChartType.GENERAL_CHART -> BarEntry(
                                    counter.toFloat(),
                                    floatArrayOf(
                                        costsValue.toFloat() * -1,
                                        profitValue.toFloat()
                                    ),
                                    month
                                )
                                ChartType.PROFIT_LOSS_CHART -> BarEntry(
                                    counter.toFloat(),
                                    (profitValue - costsValue).toFloat(),
                                    month
                                )
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

    private suspend fun getStatisticsItems(categoryType: CategoryType): List<StatisticsItem> {
        return withContext(Dispatchers.IO) {
            val items = mutableListOf<StatisticsItem>()

            val categories = when (categoryType) {
                CategoryType.CATEGORY_COSTS -> categoryRepository.allCostsCategory.first()
                CategoryType.CATEGORY_PROFIT -> categoryRepository.allProfitCategory.first()
            }

            categories.forEach {
                val item = StatisticsItem(
                    category = it,
                    barEntries = getEntriesForCategoryChart(it),
                    sumAverageCheque = 0,
                    sumAverageMonth = 0
                )
                items.add(item)
            }
            items
        }
    }

}
