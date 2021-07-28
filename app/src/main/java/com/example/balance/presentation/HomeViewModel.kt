package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.UserDataStore
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.TemplateRepository
import com.example.balance.ui.recycler_view.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates


data class HomeState(
    val sumCostsCash: Int,
    val sumCostsCards: Int,
    val sumProfitCash: Int,
    val sumProfitCards: Int
) {

    companion object {
        fun default() = HomeState(
            sumCostsCash = 0,
            sumCostsCards = 0,
            sumProfitCash = 0,
            sumProfitCards = 0
        )
    }

}

class HomeViewModel(
    val recordRepository: RecordRepository,
    val templateRepository: TemplateRepository,
    val datastore: UserDataStore
) : ViewModel() {

    val state = MutableLiveData(HomeState.default())

    val allHomeRecords = MutableLiveData<List<Item>>(listOf())

    private var sumCash: Int = 0
    private var sumCards: Int = 0

    init {
        viewModelScope.launch {
            sumCash = withContext(Dispatchers.IO) { datastore.sumCash.first() }
            sumCards = withContext(Dispatchers.IO) { datastore.sumCards.first() }
        }

        recordRepository.allRecords
            .map { newRecordList -> mapItems(newRecordList.reversed()) }
            .onEach(allHomeRecords::setValue)
            .launchIn(viewModelScope)

        recordRepository.getSum(RecordType.COSTS, MoneyType.CASH)
            .onEach { newSum ->
                state.value = state.value?.copy(
                    sumCostsCash = newSum ?: 0
                )
            }
            .launchIn(scope = viewModelScope)

        recordRepository.getSum(RecordType.COSTS, MoneyType.CARDS)
            .onEach { newSum ->
                state.value = state.value?.copy(
                    sumCostsCards = newSum ?: 0
                )
            }
            .launchIn(scope = viewModelScope)

        recordRepository.getSum(RecordType.PROFITS, MoneyType.CASH)
            .onEach { newSum ->
                state.value = state.value?.copy(
                    sumProfitCash = newSum ?: 0
                )
            }
            .launchIn(scope = viewModelScope)

        recordRepository.getSum(RecordType.PROFITS, MoneyType.CARDS)
            .onEach { newSum ->
                state.value = state.value?.copy(
                    sumProfitCards = newSum ?: 0
                )
            }
            .launchIn(scope = viewModelScope)
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
                sumCash = getCurrentCash(),
                sumCards = getCurrentCards()
            )
        )

        items.forEach { record ->
            if (record.isImportant) {
                allHomeRecords.add(
                    Item.RecordItem(
                        id = record.id,
                        date = record.date,
                        sumMoney = record.sumOfMoney,
                        recordType = record.recordType,
                        moneyType = record.moneyType,
                        category = record.category,
                        comment = record.comment,
                        isImportant = record.isImportant
                    )
                )
            }
        }
        return allHomeRecords
    }

    fun getCurrentCash(): String {
        val cash = sumCash + (state.value?.sumProfitCash ?: 0) - (state.value?.sumCostsCash ?: 0)
        return cash.toString()
    }

    fun getCurrentCards(): String {
        val cards =
            sumCards + (state.value?.sumProfitCards ?: 0) - (state.value?.sumCostsCards ?: 0)
        return cards.toString()
    }

}
