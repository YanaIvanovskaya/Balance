package com.example.balance.ui.recycler_view.item

import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.category.CategoryType
import com.example.balance.toUpperFirst
import com.example.balance.ui.recycler_view.ViewHolderFactory

class CategoryItem(
    val id: Int,
    val name: String,
    val categoryType: CategoryType,
    val dateCreation: String
) : Item {

    override fun getItemViewType(): Int {
        return Item.CATEGORY_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val categoryViewHolder = viewHolder as ViewHolderFactory.CategoryViewHolder

        categoryViewHolder.nameCategoryText.text = name.toUpperFirst()
        val dateCreation = "создано $dateCreation"
        categoryViewHolder.dateCreationText.text = dateCreation

        val resources = viewHolder.itemView.resources

        val costsColor =
            ResourcesCompat.getColor(resources, R.color.red_300, null)
        val profitsColor = ResourcesCompat.getColor(resources, R.color.green_300, null)

        categoryViewHolder.nameCategoryText.setTextColor(
            when (categoryType) {
                CategoryType.CATEGORY_COSTS -> costsColor
                CategoryType.CATEGORY_PROFIT -> profitsColor
            }
        )
    }

}