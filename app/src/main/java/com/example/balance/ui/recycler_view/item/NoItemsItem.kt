package com.example.balance.ui.recycler_view.item

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.ui.recycler_view.ViewHolderFactory

class NoItemsItem(
//    private val image: Drawable,
    private val message: String,
    private val enableAdd: Boolean = true
) : Item {
    override fun getItemViewType(): Int {
        return Item.NO_ITEMS_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val noItemViewHolder = viewHolder as ViewHolderFactory.NoItemsItemViewHolder

//        noItemViewHolder.image.setImageDrawable(image)
        noItemViewHolder.message.text = message
        if (!enableAdd) {
            noItemViewHolder.buttonAdd.visibility = View.GONE
        }
    }
}
