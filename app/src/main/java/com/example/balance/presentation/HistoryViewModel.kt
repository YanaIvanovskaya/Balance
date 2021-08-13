package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.Case
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.TemplateRepository
import com.example.balance.getMonthName
import com.example.balance.getTimeLabel
import com.example.balance.getWeekDay
import com.example.balance.ui.recycler_view.item.DateItem
import com.example.balance.ui.recycler_view.item.Item
import com.example.balance.ui.recycler_view.item.NoItemsItem
import com.example.balance.ui.recycler_view.item.RecordItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


data class HistoryState(
    val currentChip: Int,
    val allRecords: List<Item>,
    val costsRecords: List<Item>,
    val profitRecords: List<Item>,
    val importantRecords: List<Item>,
    val isContentLoaded: Boolean,
    val hasNoRecords: Boolean
) {

    companion object {
        fun default() = HistoryState(
            currentChip = 0,
            allRecords = listOf(),
            costsRecords = listOf(),
            profitRecords = listOf(),
            importantRecords = listOf(),
            isContentLoaded = false,
            hasNoRecords = true
        )
    }

}

class HistoryViewModel(
    val recordRepository: RecordRepository,
    val templateRepository: TemplateRepository,
    val categoryRepository: CategoryRepository,
) : ViewModel() {

    val state = MutableLiveData(HistoryState.default())

    init {
        recordRepository.allRecords
            .onEach { newRecordList ->
                state.value = state.value?.copy(
                    hasNoRecords = newRecordList.isEmpty()
                )
                val allRecords = mapItems(newRecordList)

                state.value = state.value?.copy(
                    allRecords = allRecords,
                    isContentLoaded = allRecords.isNotEmpty(),
                    costsRecords = mapItems(newRecordList, recordType = RecordType.COSTS),
                    profitRecords = mapItems(newRecordList, recordType = RecordType.PROFITS),
                    importantRecords = mapItems(newRecordList, onlyImportant = true)
                )

            }
            .launchIn(viewModelScope)
    }

    fun saveCurrentChip(chipNumber: Int) {
        state.value = state.value?.copy(
            currentChip = chipNumber
        )
    }

    fun removeRecord(recordId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            recordRepository.deleteRecordById(recordId)
            templateRepository.deleteTemplateByRecordId(recordId)
        }
    }

    fun onSetImportant(recordId: Int, isImportant: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            recordRepository.setImportance(recordId, isImportant = !isImportant)
        }
    }

    private suspend fun mapItems(
        items: List<Record>,
        recordType: RecordType? = null,
        onlyImportant: Boolean? = null
    ): MutableList<Item> {
        return withContext(Dispatchers.IO) {
            val allHistoryRecords: MutableList<Item> = mutableListOf()
            var currentDate = ""

            items.reversed().forEach { record ->
                val isRecordValid = if (recordType != null) {
                    record.recordType == recordType
                } else if (onlyImportant == true) {
                    record.isImportant
                } else true

                if (isRecordValid) {
                    val recordDate =
                        "${record.day} ${getMonthName(record.month, Case.OF)} ${record.year}"
                    if (currentDate.isEmpty() || currentDate != recordDate) {
                        val dateItem = DateItem(
                            date =
                            "${getWeekDay(record.weekDay)} ${record.day} ${
                                getMonthName(
                                    record.month,
                                    Case.OF
                                )
                            }"
                        )
                        allHistoryRecords.add(dateItem)
                    }

                    val sumRecord = record.sumOfMoney
                    allHistoryRecords.add(
                        RecordItem(
                            id = record.id,
                            time = getTimeLabel(record, isHistory = true),
                            sumMoney = sumRecord,
                            recordType = record.recordType,
                            moneyType = record.moneyType,
                            category = withContext(Dispatchers.IO) {
                                categoryRepository.getNameById(record.categoryId).first()
                            },
                            comment = record.comment,
                            isImportant = record.isImportant
                        )
                    )
                    currentDate = recordDate
                }
            }

            if (allHistoryRecords.isEmpty()) {
                allHistoryRecords.add(
                    when (recordType) {
                        RecordType.COSTS -> NoItemsItem(
                            message = "Здесь будет история ваших расходов",
                            enableAdd = false
                        )
                        RecordType.PROFITS -> NoItemsItem(
                            message = "Здесь будет история ваших доходов",
                            enableAdd = false
                        )
                        null -> {
                            when (onlyImportant) {
                                true -> NoItemsItem(
                                    message = "Здесь будет история ваших избранных записей",
                                    enableAdd = false
                                )
                                else -> NoItemsItem(
                                    message = "Здесь будет общая история ваших записей",
                                    enableAdd = false
                                )
                            }
                        }
                    }
                )
            }
            allHistoryRecords
        }
    }

}