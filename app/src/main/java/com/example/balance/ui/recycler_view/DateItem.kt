package com.example.balance.ui.recycler_view

import androidx.recyclerview.widget.RecyclerView
import com.example.balance.Item

class DateItem(
    private val date: String,
) : Item {

    override fun getItemViewType(): Int {
        return Item.DATE_ROW_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val dateViewHolder: ViewHolderFactory.DateViewHolder =
            viewHolder as ViewHolderFactory.DateViewHolder

        dateViewHolder.date.text = date
    }

}
