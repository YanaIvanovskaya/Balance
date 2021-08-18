package com.example.balance.presentation.home

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
import com.example.balance.getTimeLabel
import com.example.balance.ui.recycler_view.item.BalanceItem
import com.example.balance.ui.recycler_view.item.Item
import com.example.balance.ui.recycler_view.item.NoItemsItem
import com.example.balance.ui.recycler_view.item.RecordItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

data class HomeState(
    val sumCostsCash: Int,
    val sumCostsCards: Int,
    val sumProfitCash: Int,
    val sumProfitCards: Int,
    val cash: Int,
    val cards: Int,
    val isContentLoaded: Boolean,
    val isSumLoaded: Boolean,
    val hasNoRecords: Boolean
) {

    companion object {
        fun default() = HomeState(
            sumCostsCash = 0,
            sumCostsCards = 0,
            sumProfitCash = 0,
            sumProfitCards = 0,
            cash = 0,
            cards = 0,
            isContentLoaded = false,
            isSumLoaded = false,
            hasNoRecords = true
        )
    }

}

class HomeViewModel(
    val balanceRepository: BalanceRepository,
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
                val hasNoRecords = it == 0
                state.value = state.value?.copy(hasNoRecords = hasNoRecords)
                if (!hasNoRecords) {
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
                            MoneyType.CARDS
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
                }
                measureBalance()
            }.launchIn(viewModelScope)

        recordRepository.allRecords
            .map { newRecordList ->
                withContext(Dispatchers.IO) {
                    mapItems(newRecordList.reversed())
                }
            }
            .onEach(allHomeRecords::setValue)
            .launchIn(viewModelScope)
    }

    fun setContentLoaded(isContentLoaded: Boolean) {
        state.value = state.value?.copy(isContentLoaded = isContentLoaded)
    }

    fun removeRecord(recordId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            recordRepository.deleteRecordById(recordId)
            templateRepository.deleteTemplateByRecordId(recordId)
        }
    }

    fun onSetImportant(recordId: Int, isImportant: Boolean) {
        viewModelScope.launch {
            recordRepository.setImportance(recordId, isImportant = !isImportant)
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
            val lastRecords =
                if (items.size > 15) items.subList(0, 15)
                else items
            val recentlyRecords = lastRecords.filter {
                ChronoUnit.DAYS.between(
                    LocalDate.of(it.year, it.month, it.day),
                    LocalDate.now()
                ) <= 3
            }
            if (recentlyRecords.isNotEmpty()) {
                recentlyRecords.forEach { record ->
                    allHomeRecords.add(
                        RecordItem(
                            id = record.id,
                            time = getTimeLabel(record, isHistory = false),
                            sumMoney = record.sumOfMoney,
                            recordType = record.recordType,
                            moneyType = record.moneyType,
                            category = categoryRepository.getNameById(record.categoryId).first(),
                            comment = record.comment,
                            isImportant = record.isImportant
                        )
                    )
                }
            } else {
                allHomeRecords.add(NoItemsItem(message = "Пока у вас нет недавних записей"))
            }
            allHomeRecords
        }
    }

    private fun measureBalance() {
        val sumProfitCash = state.value?.sumProfitCash ?: 0
        val sumCostsCash = state.value?.sumCostsCash ?: 0
        val cash = sumCash + sumProfitCash - sumCostsCash

        val sumProfitCards = state.value?.sumProfitCards ?: 0
        val sumCostsCards = state.value?.sumCostsCards ?: 0
        val cards = sumCards + sumProfitCards - sumCostsCards

        val isSumsLoaded = !(sumProfitCash == 0
                && sumCostsCash == 0
                && sumProfitCards == 0
                && sumCostsCards == 0)

        val balanceIsLoaded = state.value?.hasNoRecords == true || isSumsLoaded
        state.value = state.value?.copy(isSumLoaded = balanceIsLoaded)

        if (balanceIsLoaded) {
            state.value = state.value?.copy(cash = cash, cards = cards)
            viewModelScope.launch(Dispatchers.IO) {
                balanceRepository.saveBalance(cash, cards)
            }
        }
    }

}
