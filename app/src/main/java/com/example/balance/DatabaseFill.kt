package com.example.balance

import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.lang.Exception

object DatabaseFill {

    private var isFilled = false

    fun fill() {
        if (isFilled) {
            return
        }

        runBlocking {
            withContext(Dispatchers.IO) {
                addCategories()
                fillDatabase()
            }
        }

        isFilled = true
    }

    private suspend fun addCategories() {
        val categories = listOf(
            Category(name = "Проезд", type = CategoryType.CATEGORY_COSTS),
            Category(name = "Одежда", type = CategoryType.CATEGORY_COSTS),
            Category(name = "Продукты", type = CategoryType.CATEGORY_COSTS),
            Category(name = "Еда не дома", type = CategoryType.CATEGORY_COSTS),
            Category(name = "Лекарства", type = CategoryType.CATEGORY_COSTS),
            Category(name = "Прочие расходы", type = CategoryType.CATEGORY_COSTS),

            Category(name = "Зарплата", type = CategoryType.CATEGORY_PROFIT),
            Category(name = "Проценты по вкладу", type = CategoryType.CATEGORY_PROFIT),
            Category(name = "Стипендия", type = CategoryType.CATEGORY_PROFIT),
        )
        categories.forEach {
            try {
                Timber.d("Insert: ${it.name}")
                BalanceApp.categoryRepository.insert(it)
                Timber.d("Insert complete")
            } catch (ex: Exception) {
                Timber.d(ex)
            }
        }
    }

    private suspend fun fillDatabase() {
        var date = LocalDate.of(2021, 1, 1)
        for (i in 1..210) {
            val newDate = date.plusDays(1L)
            date = newDate
            var isMustBeOne = false
            for (n in 1..(1..3).random()) {
                val recordType = RecordType.COSTS

                val categoryId = (1..6).random()

                val sumMoney = when (categoryId) {
                    1 -> listOf(19, 23, 120).random()
                    2 -> (200..2100).random().apply { this - this.div(10) }
                    3 -> (70..1200).random().apply { this - this.div(5) }
                    4 -> (120..800).random().apply { this - this.div(5) }
                    5 -> (200..500).random().apply { this - this.div(5) }
                    6 -> (50..500).random().apply { this - this.div(5) }
                    else -> 100
                }
                if (isMustBeOne) break
                val record = when (newDate.dayOfMonth) {
                    1 -> {
                        isMustBeOne = true
                        Record(
                            day = newDate.dayOfMonth,
                            month = newDate.month.value,
                            year = newDate.year,
                            weekDay = newDate.dayOfWeek.value,
                            isImportant = (0..1).random() == 1,
                            sumOfMoney = (200..300).random(),
                            recordType = RecordType.PROFITS,
                            moneyType = MoneyType.CARDS,
                            categoryId = 8,
                            comment = ""
                        )
                    }
                    15 -> {
                        isMustBeOne = true
                        Record(
                            day = newDate.dayOfMonth,
                            month = newDate.month.value,
                            year = newDate.year,
                            weekDay = newDate.dayOfWeek.value,
                            isImportant = (0..1).random() == 1,
                            sumOfMoney = (18000..22000).random().apply { this - this.div(10) },
                            recordType = RecordType.PROFITS,
                            moneyType = MoneyType.CARDS,
                            categoryId = 7,
                            comment = ""
                        )
                    }
                    25 -> {
                        isMustBeOne = true
                        Record(
                            day = newDate.dayOfMonth,
                            month = newDate.month.value,
                            year = newDate.year,
                            weekDay = newDate.dayOfWeek.value,
                            isImportant = (0..1).random() == 1,
                            sumOfMoney = 4300,
                            recordType = RecordType.PROFITS,
                            moneyType = MoneyType.CARDS,
                            categoryId = 9,
                            comment = ""
                        )
                    }
                    else -> {
                        isMustBeOne = false
                        Record(
                            day = newDate.dayOfMonth,
                            month = newDate.month.value,
                            year = newDate.year,
                            weekDay = newDate.dayOfWeek.value,
                            isImportant = (0..1).random() == 1,
                            sumOfMoney = sumMoney,
                            recordType = recordType,
                            moneyType = if ((0..1).random() == 1) MoneyType.CASH else MoneyType.CARDS,
                            categoryId = categoryId,
                            comment = ""
                        )
                    }
                }

                BalanceApp.recordRepository.insert(record)
            }
        }
    }

}
