package com.example.balance.ui.recycler_view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.Item
import com.example.balance.R

class HistoryRecyclerViewAdapter(
    private var dataSet: MutableList<Item>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickHeaderItemDecoration.StickyHeaderInterface {

    override fun getItemViewType(position: Int): Int {
        return dataSet[position].getItemViewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderFactory.create(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        dataSet[position].onBindViewHolder(holder)
    }

    override fun getItemCount(): Int = dataSet.size

    fun updateData(newDataSet: MutableList<Item>) {
        dataSet = newDataSet
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var position = itemPosition
        var headerPosition = 0
        do {
            if (isHeader(position)) {
                headerPosition = position
                break
            }
            position -= 1
        } while (position >= 0)
        println(headerPosition)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int): Int = R.layout.row_type_date

    override fun bindHeaderData(header: View?, headerPosition: Int) {

    }

    override fun isHeader(itemPosition: Int): Boolean =
        dataSet[itemPosition].getItemViewType() == Item.DATE_ROW_TYPE
}
