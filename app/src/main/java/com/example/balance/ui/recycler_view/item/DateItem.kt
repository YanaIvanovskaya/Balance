package com.example.balance.ui.recycler_view.item

import androidx.recyclerview.widget.RecyclerView
import com.example.balance.ui.recycler_view.ViewHolderFactory

class DateItem(
    private val date: String,
) : Item {

    override fun getItemViewType(): Int {
        return Item.DATE_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val dateViewHolder = viewHolder as ViewHolderFactory.DateViewHolder
        dateViewHolder.date.text = date
    }

}