package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.StatisticsAccessor
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.RecordRepository
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
    val profitStatItems: List<Item>,
    val costsStatItems: List<Item>,
    val mapCategoryAvgSum: Map<Int, Int>,
    val hasNoRecords: Boolean
) {

    companion object {
        fun default() = StatisticsState(
            yearsOfUse = listOf(),
            profitStatItems = mutableListOf(),
            costsStatItems = mutableListOf(),
            mapCategoryAvgSum = mutableMapOf(),
            hasNoRecords = true
        )
    }

}

class StatisticsViewModel(
    val recordRepository: RecordRepository,
    val categoryRepository: CategoryRepository
) : ViewModel() {

    val state = MutableLiveData(StatisticsState.default())

    fun init() {
        viewModelScope.launch {
            val yearsOfUse = StatisticsAccessor.getListYearsOfUse()

            if (yearsOfUse.isNotEmpty()) {
                state.value = state.value?.copy(
                    yearsOfUse = yearsOfUse,
                    hasNoRecords = false
                )

                val generalAmountOfMonths = StatisticsAccessor.getAmountMonthsOfUse()

                val categoryIds = StatisticsAccessor.getListCategoryIds()
                val mapCategoryAvgSum = mutableMapOf<Int, Int>()
                categoryIds.forEach {
                    val avgSum =
                        StatisticsAccessor.getGeneralSumCategory(it) / generalAmountOfMonths
                    mapCategoryAvgSum[it] = avgSum
                }
                state.value = state.value?.copy(
                    mapCategoryAvgSum = mapCategoryAvgSum
                )

                val profitStatItems = getStatisticsItems(CategoryType.CATEGORY_PROFIT)
                val costsStatItems = getStatisticsItems(CategoryType.CATEGORY_COSTS)

                val sumGeneralCosts = StatisticsAccessor.getSumGeneralCosts()
                val sumGeneralProfit = StatisticsAccessor.getSumGeneralProfit()

                val sumAvgMonthlyCosts = sumGeneralCosts / generalAmountOfMonths
                val sumAvgMonthlyProfit = sumGeneralProfit / generalAmountOfMonths

                val sumAvgMonthlyBalance =
                    (sumGeneralProfit - sumGeneralCosts) / generalAmountOfMonths
                val countCosts = StatisticsAccessor.getGeneralCountOfCosts()
                val amountMonthlyPurchases = countCosts / generalAmountOfMonths

                val statCostsInfoItem = StatCostsInfoItem(
                    sumGeneralCosts = sumGeneralCosts,
                    sumAvgMonthlyCosts = sumAvgMonthlyCosts,
                    amountMonthlyPurchases = amountMonthlyPurchases
                )
                val statProfitInfoItem = StatProfitInfoItem(
                    sumGeneralProfit = sumGeneralProfit,
                    sumAvgMonthlyProfit = sumAvgMonthlyProfit,
                    sumAvgMonthlyBalance = sumAvgMonthlyBalance
                )

                val costItems = mutableListOf<Item>(statCostsInfoItem)
                costItems.addAll(costsStatItems)

                val profitItems = mutableListOf<Item>(statProfitInfoItem)
                profitItems.addAll(profitStatItems)

                state.value = state.value?.copy(
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
                    val months = StatisticsAccessor.getListMonthsInYear(year)
                    for (month in months) {
                        val value = StatisticsAccessor.getMonthlySumByCategory(
                            category.id,
                            month = month,
                            year = year
                        )
                        if (value != 0) {
                            val barEntry = BarEntry(
                                counter.toFloat(),
                                value.toFloat(),
                                listOf(month, year)
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

    private suspend fun getStatisticsItems(categoryType: CategoryType): List<Item> {
        return withContext(Dispatchers.IO) {
            val items = mutableListOf<Item>()

            val categories = when (categoryType) {
                CategoryType.CATEGORY_COSTS -> categoryRepository.allCostsCategory.first()
                CategoryType.CATEGORY_PROFIT -> categoryRepository.allProfitCategory.first()
            }

            categories.forEach {
                val barEntries = getEntriesForCategoryChart(it)
                if (barEntries.isNotEmpty()) {
                    val item = CategoryChartItem(
                        category = it,
                        barEntries = barEntries,
                        sumAverageMonth = state.value?.mapCategoryAvgSum?.get(it.id) ?: 0
                    )
                    items.add(item)
                }
            }
            items
        }
    }

}
