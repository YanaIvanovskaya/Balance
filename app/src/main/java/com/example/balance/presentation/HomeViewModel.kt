package com.example.balance.presentation

import androidx.lifecycle.ViewModel
import com.example.balance.BalanceRowType
import com.example.balance.RecordRowType
import com.example.balance.RowType


class HomeViewModel : ViewModel() {

    private fun createBalanceRow() = BalanceRowType(balance = 1000, sumCash = 600, sumCards = 400)

    private fun getRecentRecords(): MutableList<RecordRowType> {
        val listOfRecords = mutableListOf<RecordRowType>()
        for (i in 0..10) {
            listOfRecords.add(
                RecordRowType("category", (15..500).random(), "$i июля 2021", (0..1).random() == 1)
            )
        }
        return listOfRecords
    }

    fun getHomeContent(): MutableList<RowType> {
        val listOfContent = mutableListOf<RowType>()
        listOfContent.add(createBalanceRow())
        for (record in getRecentRecords()) {
            listOfContent.add(record)
        }
        return listOfContent
    }

}



