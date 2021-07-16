package com.example.balance.presentation

import androidx.lifecycle.ViewModel
import com.example.balance.Item
import com.example.balance.ui.recycler_view_item.BalanceItem
import com.example.balance.ui.recycler_view_item.RecordItem

class HomeViewModel : ViewModel() {

    private fun createBalanceRow() = BalanceItem(balance = 1000, sumCash = 600, sumCards = 400)

    private fun getRecentRecords(): MutableList<RecordItem> {
        val listOfRecords = mutableListOf<RecordItem>()
        for (i in 0..10) {
            listOfRecords.add(
                RecordItem("category", (15..500).random(), "$i июля 2021", (0..1).random() == 1)
            )
        }
        return listOfRecords
    }

    fun getHomeContent(): MutableList<Item> {
        val listOfContent = mutableListOf<Item>()
        listOfContent.add(createBalanceRow())
        for (record in getRecentRecords()) {
            listOfContent.add(record)
        }
        return listOfContent
    }

}



