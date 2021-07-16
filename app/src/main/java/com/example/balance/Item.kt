package com.example.balance

import androidx.recyclerview.widget.RecyclerView

interface Item {

    companion object {
        const val BALANCE_ROW_TYPE: Int = 0
        const val RECORD_ROW_TYPE: Int = 1
        const val DATE_ROW_TYPE: Int = 2
    }

    fun getItemViewType(): Int

    fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?)

}