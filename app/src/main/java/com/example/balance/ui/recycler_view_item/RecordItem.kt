package com.example.balance.ui.recycler_view_item

import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.Item
import com.example.balance.R
import com.example.balance.presentation.ViewHolderFactory

class RecordItem(
    private val category: String,
    private val sumRecord: Int,
    private val date: String,
    private val isCosts: Boolean
) : Item {

    override fun getItemViewType(): Int {
        return Item.RECORD_ROW_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val recordViewHolder = viewHolder as ViewHolderFactory.RecordViewHolder

        recordViewHolder.categoryRecord.text = category
        recordViewHolder.sumRecord.text = sumRecord.toString()
        recordViewHolder.dateRecord.text = date

        val resources = viewHolder.itemView.resources
        val costsBackground = ResourcesCompat.getDrawable(resources, R.color.purple_200, null)
        val profitsBackground = ResourcesCompat.getDrawable(resources,R.color.teal_700,null)

        if (isCosts) {
            recordViewHolder.layout.background = costsBackground
        } else {
            recordViewHolder.layout.background = profitsBackground
        }
    }

}