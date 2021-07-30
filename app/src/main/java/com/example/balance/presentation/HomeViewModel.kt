package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.BalanceApp
import com.example.balance.data.BalanceRepository
import com.example.balance.data.UserDataStore
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.TemplateRepository
import com.example.balance.ui.recycler_view.Item
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlin.properties.Delegates

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
    val categoryRepository: CategoryRepository,
    val datastore: UserDataStore
) : ViewModel() {

    val state = MutableLiveData(HomeState.default())

    val allHomeRecords = MutableLiveData<List<Item>>(listOf())

    private var sumCash: Int = balanceRepository.sumCash
    private var sumCards: Int = balanceRepository.sumCards

    init {
        recordRepository.getSum(RecordType.COSTS, MoneyType.CASH)
            .onEach { newSum ->
                state.value = state.value?.copy(sumCostsCash = newSum)
                measureBalance()
            }
            .launchIn(scope = viewModelScope)

        recordRepository.getSum(RecordType.COSTS, MoneyType.CARDS)
            .onEach { newSum ->
                state.value = state.value?.copy(sumCostsCards = newSum)
                measureBalance()
            }
            .launchIn(scope = viewModelScope)

        recordRepository.getSum(RecordType.PROFITS, MoneyType.CASH)
            .onEach { newSum ->
                state.value = state.value?.copy(sumProfitCash = newSum)
                measureBalance()
            }
            .launchIn(scope = viewModelScope)

        recordRepository.getSum(RecordType.PROFITS, MoneyType.CARDS)
            .onEach { newSum ->
                state.value = state.value?.copy(sumProfitCards = newSum)
                measureBalance()
            }
            .launchIn(scope = viewModelScope)

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
                        date = record.dateText,
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

//    fun getCurrentCash(): String {
//        val cash = sumCash + (state.value?.sumProfitCash ?: 0) - (state.value?.sumCostsCash ?: 0)
//        return cash.toString()
//    }
//
//    fun getCurrentCards(): String {
//        val cards =
//            sumCards + (state.value?.sumProfitCards ?: 0) - (state.value?.sumCostsCards ?: 0)
//        return cards.toString()
//    }

    fun measureBalance() {
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
