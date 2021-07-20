package com.example.balance.ui.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.Category

class CategoryListAdapter :
    ListAdapter<Category, CategoryListAdapter.CategoryViewHolder>(CategoriesComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.name)
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val categoryName: TextView = itemView.findViewById(R.id.category_name)


        fun bind(name: String) {
            categoryName.text = name
        }

        companion object {
            fun create(parent: ViewGroup): CategoryViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category, parent, false)
                return CategoryViewHolder(view)
            }
        }

    }

    class CategoriesComparator : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem.name == newItem.name

    }

}