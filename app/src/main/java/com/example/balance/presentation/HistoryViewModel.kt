package com.example.balance.presentation

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.*
import com.example.balance.Item
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.template.TemplateRepository
import com.example.balance.ui.recycler_view.DateItem
import com.example.balance.ui.recycler_view.RecordItem
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HistoryViewModel(
    val recordRepository: RecordRepository,
    val templateRepository: TemplateRepository
) : ViewModel() {

    val allRecords = MutableLiveData<List<Item>>(listOf())

    init {
        recordRepository.allRecords
            .map { newRecordList -> mapItems(newRecordList.reversed()) }
            .onEach(allRecords::setValue)
            .launchIn(viewModelScope)
    }

    fun removeRecord(context: Context, recordId: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Удалить запись?")
        builder.setPositiveButton("Удалить") { _: DialogInterface, _: Int ->
            viewModelScope.launch {
                recordRepository.deleteRecordById(recordId)
                templateRepository.deleteTemplateByRecordId(recordId)
            }
        }
        builder.setNegativeButton("Закрыть", null)
        val dialog = builder.create()
        dialog.show()
    }

    fun getItems(): MutableList<Item> {
        val allHistoryRecords: MutableList<Item> = mutableListOf()
        var currentDate = ""

//        recordList.forEach { record ->
//            if (currentDate.isEmpty() || currentDate != record.date) {
//                val dateItem = DateItem(date = record.date.drop(3).dropLast(5))
//                allHistoryRecords.add(dateItem)
//            }
//            allHistoryRecords.add(
//                RecordItem(
//                    id = record.id,
//                    date = record.date,
//                    sumMoney = record.sumOfMoney,
//                    recordType = record.recordType,
//                    moneyType = record.moneyType,
//                    category = record.category,
//                    comment = record.comment
//                )
//            )
//            currentDate = record.date
//        }
        return allHistoryRecords
    }

    fun mapItems(items: List<Record>): MutableList<Item> {
        val allHistoryRecords: MutableList<Item> = mutableListOf()
        var currentDate = ""

        items.forEach { record ->
            if (currentDate.isEmpty() || currentDate != record.date) {
                val dateItem = DateItem(date = record.date.drop(3).dropLast(5))
                allHistoryRecords.add(dateItem)
            }
            allHistoryRecords.add(
                RecordItem(
                    id = record.id,
                    date = record.date,
                    sumMoney = record.sumOfMoney,
                    recordType = record.recordType,
                    moneyType = record.moneyType,
                    category = record.category,
                    comment = record.comment
                )
            )
            currentDate = record.date
        }
        return allHistoryRecords
    }

}