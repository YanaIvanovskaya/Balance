package com.example.balance.ui.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TemplateAdapter(
    var dataSet: MutableList<Item.TemplateItem> = mutableListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = dataSet[position].getItemViewType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderFactory.create(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        dataSet[position].onBindViewHolder(holder)
    }

    override fun getItemCount(): Int = dataSet.size

    fun removeAt(position: Int) {
        dataSet.removeAt(position)
        notifyItemChanged(position)
    }

}