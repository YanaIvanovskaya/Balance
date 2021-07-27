package com.example.balance.presentation

import androidx.lifecycle.*
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.UserDataStore
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.RecordType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking


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
    recordRepository: RecordRepository,
    val datastore: UserDataStore
) : ViewModel() {

    val state = MutableLiveData(HomeState.default())

    val allRecords: LiveData<List<Record>> = recordRepository.allRecords
        .asLiveData()
        .let { allRecords ->
            Transformations.map(allRecords) {
                it.filter { record -> record.isImportant }.reversed()
            }
        }

    private val sumCash = runBlocking { datastore.sumCash.first() }
    private val sumCards = runBlocking { datastore.sumCards.first() }

    init {
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
