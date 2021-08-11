package com.example.balance.ui.recycler_view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.ui.recycler_view.ViewHolderFactory
import com.example.balance.ui.recycler_view.item.BalanceItem
import com.example.balance.ui.recycler_view.item.Item
import com.example.balance.ui.recycler_view.item.RecordItem
import timber.log.Timber

class HomeAdapter(
    var dataSet: MutableList<Item> = mutableListOf(),
    private val onLongItemClickListener: (recordId: Int,isImportant:Boolean) -> Boolean
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
                onLongItemClickListener(recordItem.id, recordItem.isImportant)
                true
            }
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

//class ItemDiffUtilCallback(val oldList: List<Item>, val newList: List<Item>) :
//    DiffUtil.Callback() {
//
//    override fun getOldListSize(): Int = oldList.size
//
//    override fun getNewListSize(): Int = newList.size
//
//    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        val oldItem = oldList[oldItemPosition]
//        val newItem = oldList[newItemPosition]
//        return when {
//            oldItem is RecordItem && newItem is Item.RecordItem -> {
//                oldItem.id == newItem.id
//            }
//            else -> false
//        }
//    }
//
//    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        val oldItem = oldList[oldItemPosition]
//        val newItem = oldList[newItemPosition]
//        return when {
//            oldItem is Item.RecordItem && newItem is Item.RecordItem -> {
//                oldItem.sumMoney == newItem.sumMoney &&
//                        oldItem.recordType == newItem.recordType &&
//                        oldItem.moneyType == newItem.moneyType &&
//                        oldItem.category == newItem.category &&
//                        oldItem.comment == newItem.comment &&
//                        oldItem.isImportant == newItem.isImportant
//            }
//            else -> false
//        }
//    }
//}