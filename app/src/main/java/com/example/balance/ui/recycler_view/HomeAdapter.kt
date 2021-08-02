package com.example.balance.ui.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class HomeAdapter(
    var dataSet: MutableList<Item> = mutableListOf(),
    private val onLongItemClickListener: (recordId: Int) -> Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = dataSet[position].getItemViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderFactory.create(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataSet[position]
        if (item is Item.RecordItem) {
            val recordItem: Item.RecordItem = item
            val recordViewHolder: ViewHolderFactory.RecordViewHolder =
                holder as ViewHolderFactory.RecordViewHolder
            recordViewHolder.layout.setOnLongClickListener {
                onLongItemClickListener(recordItem.id)
                println(position)
                true
            }
        }
        item.onBindViewHolder(holder)
    }

    override fun getItemCount(): Int = dataSet.size

    fun updateBalance(cash: String, cards: String) {
        Timber.d("Cash: $cash, Cards: $cards")
        if (dataSet.isNotEmpty()) {
            val item = dataSet[0]
            if (item is Item.BalanceItem) {
                val balanceItem: Item.BalanceItem = item
                balanceItem.update(cash, cards)
            }
        }
        notifyItemChanged(0)
    }

}

class ItemDiffUtilCallback(val oldList: List<Item>, val newList: List<Item>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = oldList[newItemPosition]
        return when {
            oldItem is Item.RecordItem && newItem is Item.RecordItem -> {
                oldItem.id == newItem.id
            }
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = oldList[newItemPosition]
        return when {
            oldItem is Item.RecordItem && newItem is Item.RecordItem -> {
                oldItem.sumMoney == newItem.sumMoney &&
                        oldItem.recordType == newItem.recordType &&
                        oldItem.moneyType == newItem.moneyType &&
                        oldItem.category == newItem.category &&
                        oldItem.comment == newItem.comment &&
                        oldItem.isImportant == newItem.isImportant
            }
            else -> false
        }
    }
}