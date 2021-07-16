package com.example.balance.presentation

import androidx.lifecycle.ViewModel
import com.example.balance.Item
import com.example.balance.ui.recycler_view_item.DateItem
import com.example.balance.ui.recycler_view_item.RecordItem

class HistoryViewModel: ViewModel() {

    fun getHistoryContent(): MutableList<Item> {
        val listOfContent = mutableListOf<Item>()
        for (i in 0..10) {
            listOfContent.add(
                DateItem( "$i июля 2021")
            )
            listOfContent.add(
                RecordItem("category", (15..500).random(), "$i июля 2021", (0..1).random() == 1)
            )
        }
        return listOfContent
    }

}