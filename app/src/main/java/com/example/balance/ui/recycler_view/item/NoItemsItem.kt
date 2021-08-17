package com.example.balance.ui.recycler_view.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.ui.recycler_view.ViewHolderFactory

class NoItemsItem(
    private val message: String,
    private val enableAdd: Boolean = true
) : Item {
    override fun getItemViewType(): Int {
        return Item.NO_ITEMS_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val noItemViewHolder = viewHolder as ViewHolderFactory.NoItemsItemViewHolder
        noItemViewHolder.apply {
            this.messageText.text = message
            if (!enableAdd)
                buttonAdd.visibility = View.GONE
        }
    }

}
