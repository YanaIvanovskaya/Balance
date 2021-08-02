package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.BalanceRepository
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.TemplateRepository
import com.example.balance.getTime
import com.example.balance.ui.recycler_view.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber

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
                Timber.w("onEach start ${System.currentTimeMillis()}")

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
                Timber.w("onEach end ${System.currentTimeMillis()}")
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

    private fun mapItems(items: List<Record>): MutableList<Item> {
        val allHomeRecords: MutableList<Item> = mutableListOf()

        allHomeRecords.add(
            Item.BalanceItem(
                sumCash = state.value?.cash.toString(),
                sumCards = state.value?.cards.toString()
            )
        )

        items.forEach { record ->
            if (record.isImportant) {
                allHomeRecords.add(
                    Item.RecordItem(
                        id = record.id,
                        date = getTime(record.time),
                        sumMoney = record.sumOfMoney,
                        recordType = record.recordType,
                        moneyType = record.moneyType,
                        category = runBlocking {
                            categoryRepository.getNameById(record.categoryId).first()
                        },
                        comment = record.comment,
                        isImportant = record.isImportant
                    )
                )
            }
        }
        return allHomeRecords
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
