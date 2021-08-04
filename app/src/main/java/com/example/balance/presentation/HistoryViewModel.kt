package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.Case
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.template.TemplateRepository
import com.example.balance.getMonthName
import com.example.balance.getTime
import com.example.balance.ui.recycler_view.item.DateItem
import com.example.balance.ui.recycler_view.item.Item
import com.example.balance.ui.recycler_view.item.RecordItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HistoryViewModel(
    val recordRepository: RecordRepository,
    val templateRepository: TemplateRepository,
    val categoryRepository: CategoryRepository,
) : ViewModel() {

    val allHistoryRecords = MutableLiveData<List<Item>>(listOf())

    init {
        recordRepository.allRecords
            .map { newRecordList -> mapItems(newRecordList) }
            .onEach(allHistoryRecords::setValue)
            .launchIn(viewModelScope)
    }

    fun removeRecord(recordId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            recordRepository.deleteRecordById(recordId)
            templateRepository.deleteTemplateByRecordId(recordId)
        }
    }

    fun onPinClick(recordId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            recordRepository.setImportance(recordId, isImportant = true)
        }
    }

    private fun mapItems(items: List<Record>): MutableList<Item> {
        val allHistoryRecords: MutableList<Item> = mutableListOf()
        var currentDate = ""
        items.reversed().forEach { record ->
            val recordDate = "${record.day} ${getMonthName(record.month, Case.OF)} ${record.year}"
            if (currentDate.isEmpty() || currentDate != recordDate) {
                val dateItem = DateItem(
                    date =
                    "${record.day} ${getMonthName(record.month, Case.OF)}"
                )
                allHistoryRecords.add(dateItem)
            }

            val sumRecord = record.sumOfMoney
            allHistoryRecords.add(
                RecordItem(
                    id = record.id,
                    date = getTime(record.time),
                    sumMoney = sumRecord,
                    recordType = record.recordType,
                    moneyType = record.moneyType,
                    category = runBlocking {
                        categoryRepository.getNameById(record.categoryId).first()
                    },
                    comment = record.comment,
                    isImportant = record.isImportant
                )
            )
            currentDate = recordDate
        }

        return allHistoryRecords
    }

}