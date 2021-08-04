package com.example.balance.ui.recycler_view.item

import androidx.recyclerview.widget.RecyclerView

sealed interface Item {

    companion object {
        const val BALANCE_ITEM_TYPE: Int = 0
        const val RECORD_ITEM_TYPE: Int = 1
        const val DATE_ITEM_TYPE: Int = 2
        const val TEMPLATE_ITEM_TYPE = 3
        const val CATEGORY_ITEM_TYPE = 4
        const val STATISTICS_ITEM_TYPE = 5
    }

    fun getItemViewType(): Int

    fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?)

}


