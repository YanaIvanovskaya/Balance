package com.example.balance.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.Item

// TODO: 16.07.2021 Перенести в UI
class RecyclerViewAdapter(
    private val dataSet: List<Item>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return dataSet[position].getItemViewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderFactory.create(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        dataSet[position].onBindViewHolder(holder)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}