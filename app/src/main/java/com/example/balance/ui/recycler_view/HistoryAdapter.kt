package com.example.balance.ui.recycler_view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R


class HistoryAdapter(
    var dataSet: MutableList<Item> = mutableListOf(),
    private val onLongItemClickListener: (recordId: Int, isImportant: Boolean) -> Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickHeaderItemDecoration.StickyHeaderInterface {

    override fun getItemViewType(position: Int): Int = dataSet[position].getItemViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolderFactory.create(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataSet[position]
        if (item is Item.RecordItem) {
            val recordItem: Item.RecordItem = item
            val recordViewHolder: ViewHolderFactory.RecordViewHolder =
                holder as ViewHolderFactory.RecordViewHolder
            recordViewHolder.layout.setOnLongClickListener {
                onLongItemClickListener(recordItem.id, recordItem.isImportant)
                true
            }
        }
        item.onBindViewHolder(holder)
    }

    override fun getItemCount(): Int = dataSet.size


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
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int): Int = R.layout.item_date

    override fun bindHeaderData(header: View?, headerPosition: Int) {}

    override fun isHeader(itemPosition: Int): Boolean =
        dataSet[itemPosition].getItemViewType() == Item.DATE_ITEM_TYPE

}
