package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.ui.recycler_view.item.CategoryChartItem
import com.example.balance.ui.recycler_view.item.Item
import com.example.balance.ui.recycler_view.item.StatCostsInfoItem
import com.example.balance.ui.recycler_view.item.StatProfitInfoItem
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class StatisticsState(
    val yearsOfUse: List<Int>,
    val entriesCommonChart: List<BarEntry>,
    val entriesRestChart: List<BarEntry>,
    val profitStatItems: List<Item>,
    val costsStatItems: List<Item>,
    val mapCategoryAvgSum: Map<Int, Int>
) {

    companion object {
        fun default() = StatisticsState(
            yearsOfUse = listOf(),
            entriesCommonChart = mutableListOf(),
            entriesRestChart = mutableListOf(),
            profitStatItems = mutableListOf(),
            costsStatItems = mutableListOf(),
            mapCategoryAvgSum = mutableMapOf()
        )
    }

}

enum class ChartType {
    GENERAL_CHART,
    PROFIT_LOSS_CHART
}

class StatisticsViewModel(
    val recordRepository: RecordRepository,
    val categoryRepository: CategoryRepository
) : ViewModel() {

    val state = MutableLiveData(StatisticsState.default())

    fun init() {
        viewModelScope.launch {
            val yearsOfUse =
                withContext(Dispatchers.IO) { recordRepository.getYearsOfUse().first() }
            if (yearsOfUse.isNotEmpty()) {
                state.value = state.value?.copy(yearsOfUse = yearsOfUse)

                var generalAmountOfMonths = 0
                yearsOfUse.forEach {
                    generalAmountOfMonths += withContext(Dispatchers.IO) {
                        recordRepository.getMonthsInYear(it).first().size
                    }
                }

                val categoryIds = withContext(Dispatchers.IO) {
                    categoryRepository.allCategories.first().map { it.id }
                }
                val mapCategoryAvgSum = mutableMapOf<Int, Int>()
                categoryIds.forEach {
                    val avgSum = withContext(Dispatchers.IO) {
                        recordRepository.getSumByCategoryId(it).first()
                    } / generalAmountOfMonths
                    mapCategoryAvgSum[it] = avgSum
                }
                state.value = state.value?.copy(
                    mapCategoryAvgSum = mapCategoryAvgSum
                )

                val entriesCommonChart = getEntriesForStaticChart(ChartType.GENERAL_CHART)
                val entriesRestChart = getEntriesForStaticChart(ChartType.PROFIT_LOSS_CHART)
                val profitStatItems = getStatisticsItems(CategoryType.CATEGORY_PROFIT)
                val costsStatItems = getStatisticsItems(CategoryType.CATEGORY_COSTS)

                val sumGeneralCosts = withContext(Dispatchers.IO) {
                    recordRepository.getSum(RecordType.COSTS).first()
                }
                val sumGeneralProfit = withContext(Dispatchers.IO) {
                    recordRepository.getSum(RecordType.PROFITS).first()
                }

                val sumAvgMonthlyCosts = sumGeneralCosts / generalAmountOfMonths
                val sumAvgMonthlyProfit = sumGeneralProfit / generalAmountOfMonths

                val sumAvgMonthlyBalance =
                    (sumGeneralProfit - sumGeneralCosts) / generalAmountOfMonths
                val countCosts = withContext(Dispatchers.IO) {
                    recordRepository.getRecordsByType(RecordType.COSTS).first().size
                }
                val amountMonthlyPurchases = countCosts / generalAmountOfMonths

                val statCostsInfoItem = StatCostsInfoItem(
                    sumGeneralCosts = sumGeneralCosts,
                    sumAvgMonthlyCosts = sumAvgMonthlyCosts,
                    amountMonthlyPurchases = amountMonthlyPurchases,
                    percentAvgMonthlyCosts = 0
                )
                val statProfitInfoItem = StatProfitInfoItem(
                    sumGeneralProfit = sumGeneralProfit,
                    sumAvgMonthlyProfit = sumAvgMonthlyProfit,
                    sumAvgMonthlyBalance = sumAvgMonthlyBalance,
                    percentAvgMonthlyProfit = 0
                )

                val costItems = mutableListOf<Item>(statCostsInfoItem)
                costItems.addAll(costsStatItems)

                val profitItems = mutableListOf<Item>(statProfitInfoItem)
                profitItems.addAll(profitStatItems)

                state.value = state.value?.copy(
                    entriesCommonChart = entriesCommonChart,
                    entriesRestChart = entriesRestChart,
                    profitStatItems = profitItems,
                    costsStatItems = costItems
                )
            }
        }
    }

    fun getProfitStatItems(): List<Item> = state.value?.profitStatItems ?: listOf()

    fun getCostsStatItems(): List<Item> = state.value?.costsStatItems ?: listOf()

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

    private suspend fun getStatisticsItems(categoryType: CategoryType): List<Item> {
        return withContext(Dispatchers.IO) {
            val items = mutableListOf<Item>()

            val categories = when (categoryType) {
                CategoryType.CATEGORY_COSTS -> categoryRepository.allCostsCategory.first()
                CategoryType.CATEGORY_PROFIT -> categoryRepository.allProfitCategory.first()
            }

            categories.forEach {
                val item = CategoryChartItem(
                    category = it,
                    barEntries = getEntriesForCategoryChart(it),
                    sumAverageMonth = state.value?.mapCategoryAvgSum?.get(it.id) ?: 0
                )
                items.add(item)
            }
            items
        }
    }

}
