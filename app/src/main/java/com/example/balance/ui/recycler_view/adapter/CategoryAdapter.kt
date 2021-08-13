package com.example.balance.ui.recycler_view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.ui.recycler_view.item.CategoryItem
import com.example.balance.ui.recycler_view.ViewHolderFactory
import com.example.balance.ui.recycler_view.item.Item
import com.example.balance.ui.recycler_view.item.NoItemsItem
import com.example.balance.ui.recycler_view.item.RecordItem

class CategoryAdapter(
    var dataSet: MutableList<Item> = mutableListOf(),
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = dataSet[position].getItemViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolderFactory.create(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        dataSet[position].onBindViewHolder(holder)

    override fun getItemCount(): Int = dataSet.size

    fun removeAt(position: Int) {
        dataSet.removeAt(position)
        notifyDataSetChanged()
    }

    fun insertAt(position: Int, item: CategoryItem) {
        dataSet.add(position, item)
        notifyDataSetChanged()
    }

}