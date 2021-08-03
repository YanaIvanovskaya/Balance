package com.example.balance.ui.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    var dataSet: MutableList<Item.CategoryItem> = mutableListOf()
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

    fun insertAt(position: Int,item: Item.CategoryItem) {
        dataSet.add(position,item)
        notifyDataSetChanged()
    }

}