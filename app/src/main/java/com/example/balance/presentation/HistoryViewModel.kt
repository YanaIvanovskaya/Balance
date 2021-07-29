package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.BalanceApp
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.template.TemplateRepository
import com.example.balance.ui.recycler_view.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel(
    val recordRepository: RecordRepository,
    val templateRepository: TemplateRepository
) : ViewModel() {

    val allHistoryRecords = MutableLiveData<List<Item>>(listOf())

    init {
        recordRepository.allRecords
            .map { newRecordList -> mapItems(newRecordList.reversed()) }
            .onEach(allHistoryRecords::setValue)
            .launchIn(viewModelScope)
    }

    fun removeRecord(recordId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            recordRepository.deleteRecordById(recordId)
            templateRepository.deleteTemplateByRecordId(recordId)
        }
    }

    fun onPinClick( recordId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            recordRepository.setImportance(recordId,isImportant = true)
        }
    }


    private fun mapItems(items: List<Record>): MutableList<Item> {
        val allHistoryRecords: MutableList<Item> = mutableListOf()
        var currentDate = ""
        viewModelScope.launch {
            items.forEach { record ->
                if (currentDate.isEmpty() || currentDate != record.dateText) {
                    val dateItem = Item.DateItem(date = record.dateText.drop(3).dropLast(5))
                    allHistoryRecords.add(dateItem)
                }
                val recordCategory =
                    withContext(Dispatchers.IO) {
                        BalanceApp.categoryRepository.getNameById(record.categoryId).first()
                    }
                allHistoryRecords.add(
                    Item.RecordItem(
                        id = record.id,
                        date = record.dateText,
                        sumMoney = record.sumOfMoney,
                        recordType = record.recordType,
                        moneyType = record.moneyType,
                        category = recordCategory,
                        comment = record.comment,
                        isImportant = record.isImportant
                    )
                )
                currentDate = record.dateText
            }
        }
        return allHistoryRecords
    }

}