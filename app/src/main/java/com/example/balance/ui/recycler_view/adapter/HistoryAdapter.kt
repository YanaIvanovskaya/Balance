package com.example.balance.ui.recycler_view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.ui.recycler_view.ViewHolderFactory
import com.example.balance.ui.recycler_view.item.DateItem
import com.example.balance.ui.recycler_view.item.Item
import com.example.balance.ui.recycler_view.item.RecordItem

class HistoryAdapter(
    var dataSet: List<Item> = mutableListOf(),
    private val onLongItemClickListener: (recordId: Int, position: Int, isImportant: Boolean) -> Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = dataSet[position].getItemViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolderFactory.create(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataSet[position]
        if (item is RecordItem) {
            val recordItem: RecordItem = item
            val recordViewHolder: ViewHolderFactory.RecordViewHolder =
                holder as ViewHolderFactory.RecordViewHolder
            recordViewHolder.layout.setOnLongClickListener {
                onLongItemClickListener(recordItem.id, position, recordItem.isImportant)
                true
            }
        }
        item.onBindViewHolder(holder)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemId(position: Int): Long {
        return when (val item = dataSet[position]) {
            is RecordItem -> item.id.toLong()
            is DateItem -> {
                var code = 0
                item.date.forEach {
                    code += it.code
                }
                code.toLong()
            }
            else -> 0
        }
    }

}
