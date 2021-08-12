package com.example.balance.ui.recycler_view.item

import androidx.recyclerview.widget.RecyclerView

class NoRecentlyRecordsItem: Item {
    override fun getItemViewType(): Int {
        return Item.NO_RECENTLY_RECORDS_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        TODO("Not yet implemented")
    }
}