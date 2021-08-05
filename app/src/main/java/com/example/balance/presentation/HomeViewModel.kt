package com.example.balance.presentation

import androidx.core.util.rangeTo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.Case
import com.example.balance.data.BalanceRepository
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.TemplateRepository
import com.example.balance.getMonthName
import com.example.balance.getTime
import com.example.balance.ui.recycler_view.item.BalanceItem
import com.example.balance.ui.recycler_view.item.Item
import com.example.balance.ui.recycler_view.item.RecordItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

data class HomeState(
    val sumCostsCash: Int,
    val sumCostsCards: Int,
    val sumProfitCash: Int,
    val sumProfitCards: Int,
    val cash: Int,
    val cards: Int,
) {

    companion object {
        fun default() = HomeState(
            sumCostsCash = 0,
            sumCostsCards = 0,
            sumProfitCash = 0,
            sumProfitCards = 0,
            cash = 0,
            cards = 0
        )
    }

}

class HomeViewModel(
    balanceRepository: BalanceRepository,
    val recordRepository: RecordRepository,
    val templateRepository: TemplateRepository,
    val categoryRepository: CategoryRepository
) : ViewModel() {

    val state = MutableLiveData(HomeState.default())

    val allHomeRecords = MutableLiveData<List<Item>>(listOf())

    private var sumCash: Int = balanceRepository.sumCash
    private var sumCards: Int = balanceRepository.sumCards

    init {
        recordRepository.getCommonSum()
            .distinctUntilChanged()
            .onEach {
                val sumCostsCash = withContext(Dispatchers.IO) {
                    recordRepository.getSum(
                        RecordType.COSTS,
                        MoneyType.CASH
                    ).first()
                }

                val sumProfitCash = withContext(Dispatchers.IO) {
                    recordRepository.getSum(
                        RecordType.PROFITS,
                        MoneyType.CASH
                    ).first()
                }

                val sumCostsCards = withContext(Dispatchers.IO) {
                    recordRepository.getSum(
                        RecordType.COSTS,
                        MoneyType.CASH
                    ).first()
                }

                val sumProfitCards = withContext(Dispatchers.IO) {
                    recordRepository.getSum(
                        RecordType.PROFITS,
                        MoneyType.CARDS
                    ).first()
                }

                state.value = state.value?.copy(
                    sumCostsCash = sumCostsCash,
                    sumCostsCards = sumCostsCards,
                    sumProfitCash = sumProfitCash,
                    sumProfitCards = sumProfitCards,
                )

                measureBalance()
            }.launchIn(viewModelScope)

        recordRepository.allRecords
            .map { newRecordList -> mapItems(newRecordList.reversed()) }
            .onEach(allHomeRecords::setValue)
            .launchIn(viewModelScope)
    }

    fun removeRecord(recordId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            recordRepository.deleteRecordById(recordId)
            templateRepository.deleteTemplateByRecordId(recordId)
        }
    }

    fun onUnpinClick(recordId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            recordRepository.setImportance(recordId, isImportant = false)
        }
    }

    private suspend fun mapItems(items: List<Record>): MutableList<Item> {
        return withContext(Dispatchers.IO) {
            val allHomeRecords: MutableList<Item> = mutableListOf()
            allHomeRecords.add(
                BalanceItem(
                    sumCash = state.value?.cash.toString(),
                    sumCards = state.value?.cards.toString()
                )
            )
            val currentDateTime = LocalDateTime.now()

            val currentDate = currentDateTime.toLocalDate()

            val recentlyRecords = when (items.size) {
                in 1..15 -> items
                else -> items.subList(0,15)
            }

            recentlyRecords.forEach { record ->
                val recordDate = LocalDate.of(record.year, record.month, record.day)

                val timeOfRecord = if (currentDate == recordDate) {
                    "today"
                } else if (currentDate.minusDays(1) == recordDate) {
                    "yesterday"
                } else {
                    "${record.day} ${getMonthName(record.month, Case.OF)} ${record.year}"
                }

//                if (currentDateTime.dayOfMonth == record.day
//                    && currentDateTime.month.value == record.month
//                    && currentDateTime.year == record.year
//                )

                allHomeRecords.add(
                    RecordItem(
                        id = record.id,
                        time = timeOfRecord,
                        sumMoney = record.sumOfMoney,
                        recordType = record.recordType,
                        moneyType = record.moneyType,
                        category = categoryRepository.getNameById(record.categoryId).first(),
                        comment = record.comment,
                        isImportant = record.isImportant
                    )
                )

            }
            allHomeRecords
        }
    }

    private fun measureBalance() {
        Timber.d("Measure: cash: $sumCash cars: $sumCards")
        val cash = sumCash +
                (state.value?.sumProfitCash ?: 0) -
                (state.value?.sumCostsCash ?: 0)

        val cards = sumCards +
                (state.value?.sumProfitCards ?: 0) -
                (state.value?.sumCostsCards ?: 0)

        state.value = state.value?.copy(cash = cash, cards = cards)
    }

}
