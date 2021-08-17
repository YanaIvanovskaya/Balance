package com.example.balance.ui.recycler_view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.ui.recycler_view.ViewHolderFactory
import com.example.balance.ui.recycler_view.item.BalanceItem
import com.example.balance.ui.recycler_view.item.Item
import com.example.balance.ui.recycler_view.item.NoItemsItem
import com.example.balance.ui.recycler_view.item.RecordItem

class HomeAdapter(
    var dataSet: MutableList<Item> = mutableListOf(),
    private val onLongItemClickListener: (recordId: Int, isImportant: Boolean) -> Boolean,
    private val onClickAddListener: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = dataSet[position].getItemViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolderFactory.create(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataSet[position]

        when (item) {
            is RecordItem -> {
                val recordItem: RecordItem = item
                val recordViewHolder: ViewHolderFactory.RecordViewHolder =
                    holder as ViewHolderFactory.RecordViewHolder
                recordViewHolder.layout.setOnLongClickListener {
                    onLongItemClickListener(recordItem.id, recordItem.isImportant)
                    true
                }
            }
            is NoItemsItem -> {
                val noItemViewHolder: ViewHolderFactory.NoItemsItemViewHolder =
                    holder as ViewHolderFactory.NoItemsItemViewHolder
                noItemViewHolder.buttonAdd.setOnClickListener {
                    onClickAddListener()
                }
            }
            else -> Unit
        }
        item.onBindViewHolder(holder)
    }

    override fun getItemCount(): Int = dataSet.size

    fun updateBalance(cash: String, cards: String) {
        if (dataSet.isNotEmpty()) {
            val item = dataSet[0]
            if (item is BalanceItem) {
                val balanceItem: BalanceItem = item
                balanceItem.update(cash, cards)
            }
        }
        notifyItemChanged(0)
    }

}