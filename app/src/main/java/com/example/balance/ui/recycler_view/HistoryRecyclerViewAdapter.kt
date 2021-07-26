package com.example.balance.ui.recycler_view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.Item
import com.example.balance.R

class HistoryRecyclerViewAdapter(
    private var dataSet: MutableList<Item>,
    private val onItemClickListener: (recordId: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickHeaderItemDecoration.StickyHeaderInterface {


    override fun getItemViewType(position: Int): Int {
        return dataSet[position].getItemViewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderFactory.create(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataSet[position]
        val cond = item::class == RecordItem::class
        if (cond) {
            val recordItem: RecordItem = item as RecordItem
            val recordViewHolder: ViewHolderFactory.RecordViewHolder = holder as ViewHolderFactory.RecordViewHolder
            recordViewHolder.buttonEdit.setOnClickListener { onItemClickListener(recordItem.id) }
        }
        dataSet[position].onBindViewHolder(holder)
    }

    override fun getItemCount(): Int = dataSet.size

    fun removeAt(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

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
