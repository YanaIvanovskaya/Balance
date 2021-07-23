package com.example.balance.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.balance.Item
import com.example.balance.data.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.ui.recycler_view.DateItem
import com.example.balance.ui.recycler_view.RecordItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class HistoryViewModel(
    val repository: RecordRepository
) : ViewModel() {

    val allRecords: LiveData<List<Record>> = repository.allRecords.asLiveData()

    var recordList: List<Record> = runBlocking {
        repository.allRecords.first().reversed()
    }

    fun getItems(): MutableList<Item> {
        val allHistoryRecords: MutableList<Item> = mutableListOf()
        var currentDate = ""

        recordList.forEach { record ->
            if (currentDate.isEmpty() || currentDate != record.date) {
                val dateItem = DateItem(date = record.date.drop(3).dropLast(5))
                allHistoryRecords.add(dateItem)
            }
            allHistoryRecords.add(
                RecordItem(
                    date = record.date,
                    sumMoney = record.sumOfMoney,
                    recordType = record.recordType,
                    moneyType = record.moneyType,
                    category = record.category
                )
            )
            currentDate = record.date
        }
        println(allHistoryRecords)
        return allHistoryRecords
    }
}