package com.example.balance.ui.recycler_view.item

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.RecordType
import com.example.balance.ui.recycler_view.ViewHolderFactory

class RecordItem(
    val id: Int,
    val time: String,
    val sumMoney: Int,
    val recordType: RecordType,
    val moneyType: MoneyType,
    val category: String,
    val comment: String,
    val isImportant: Boolean
) : Item {

    override fun getItemViewType(): Int {
        return Item.RECORD_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val recordViewHolder = viewHolder as ViewHolderFactory.RecordViewHolder

        recordViewHolder.dateText.text = time
        recordViewHolder.sumText.text = sumMoney.toString()
        recordViewHolder.categoryText.text = category

        val resources = viewHolder.itemView.resources

        val imageCash =
            ResourcesCompat.getDrawable(resources, R.drawable.coins, null)
        val imageCards =
            ResourcesCompat.getDrawable(resources, R.drawable.credit_card, null)

        val costsColor =
            ResourcesCompat.getDrawable(resources, android.R.color.holo_red_light, null)
        val profitsColor = ResourcesCompat.getDrawable(resources, R.color.teal_700, null)

        when (moneyType) {
            MoneyType.CASH -> recordViewHolder.moneyTypeText.setImageDrawable(imageCash)
            MoneyType.CARDS -> recordViewHolder.moneyTypeText.setImageDrawable(imageCards)
        }

        when (recordType) {
            RecordType.COSTS -> recordViewHolder.layout.background = costsColor
            RecordType.PROFITS -> recordViewHolder.layout.background = profitsColor
        }

        recordViewHolder.imageImportant.isVisible = isImportant

        if (comment.isNotEmpty()) {
            recordViewHolder.buttonShowComment.visibility = View.VISIBLE
            recordViewHolder.comment.setText(comment)
        }

        recordViewHolder.buttonShowComment.setOnClickListener {
            when (recordViewHolder.comment.visibility) {
                View.VISIBLE -> recordViewHolder.comment.visibility = View.GONE
                else -> recordViewHolder.comment.visibility = View.VISIBLE
            }
        }

    }

}
