package com.example.balance.data

import com.example.balance.BalanceApp
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.RecordType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class StatisticsAccessor {

    companion object {
        private val recordRepository = BalanceApp.recordRepository
        private val categoryRepository = BalanceApp.categoryRepository

        suspend fun getListYearsOfUse(): List<Int> {
            return withContext(Dispatchers.IO) {
                recordRepository.getYearsOfUse().first()
            }
        }

        suspend fun getAmountMonthsOfUse(): Int {
            var generalAmountOfMonths = 0
            getListYearsOfUse().forEach {
                generalAmountOfMonths += withContext(Dispatchers.IO) {
                    recordRepository.getMonthsInYear(it).first().size
                }
            }
            return generalAmountOfMonths
        }

        suspend fun getListCategoryIds(): List<Int> {
            return withContext(Dispatchers.IO) {
                categoryRepository.allCategories.first().map { it.id }
            }
        }

        suspend fun getListCategoryWithSum(categoryType: CategoryType): List<Pair<String, Int>> {
            return withContext(Dispatchers.IO) {
                val mapCategoryWithSum = mutableListOf<Pair<String, Int>>()
                val categories = when (categoryType) {
                    CategoryType.CATEGORY_COSTS -> categoryRepository.allCostsCategory
                    else -> categoryRepository.allProfitCategory
                }
                categories.first().forEach {
                    mapCategoryWithSum.add(it.name to getGeneralSumCategory(it.id))
                }
                mapCategoryWithSum
            }
        }

        suspend fun getSumGeneralCosts(): Int {
            return withContext(Dispatchers.IO) {
                recordRepository.getSum(RecordType.COSTS).first()
            }
        }

        suspend fun getSumGeneralProfit(): Int {
            return withContext(Dispatchers.IO) {
                recordRepository.getSum(RecordType.PROFITS).first()
            }
        }

        suspend fun getGeneralCountOfCosts(): Int {
            return withContext(Dispatchers.IO) {
                recordRepository.getRecordsByType(RecordType.COSTS).first().size
            }
        }

        suspend fun getGeneralSumCategory(categoryId: Int): Int {
            return withContext(Dispatchers.IO) {
                recordRepository.getSumByCategoryId(categoryId).first()
            }
        }

        suspend fun getMonthlySumByCategory(categoryId: Int, month: Int, year: Int): Int {
            return withContext(Dispatchers.IO) {
                recordRepository.getMonthlyAmount(
                    categoryId = categoryId,
                    month = month,
                    year = year
                ).first() ?: 0
            }
        }

        suspend fun getMonthlySumByRecordType(recordType: RecordType, month: Int, year: Int): Int {
            return withContext(Dispatchers.IO) {
                recordRepository.getMonthlyAmount(
                    recordType = recordType,
                    month = month,
                    year = year
                ).first() ?: 0
            }
        }

        suspend fun getListMonthsInYear(year: Int): List<Int> {
            return withContext(Dispatchers.IO) {
                recordRepository.getMonthsInYear(year).first()
            }
        }


    }
}