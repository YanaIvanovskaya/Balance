package com.example.balance.ui.recycler_view

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.Item
import com.example.balance.R
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.RecordType

class RecordItem(
    val id: Int,
    private val date: String,
    private val sumMoney: Int,
    private val recordType: RecordType,
    private val moneyType: MoneyType,
    private val category: String,
    private val comment: String
) : Item {

    override fun getItemViewType(): Int {
        return Item.RECORD_ROW_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val recordViewHolder = viewHolder as ViewHolderFactory.RecordViewHolder

        recordViewHolder.dateText.text = date
        recordViewHolder.sumText.text = sumMoney.toString()
        recordViewHolder.categoryText.text = category

        val resources = viewHolder.itemView.resources

        val imageCash =
            ResourcesCompat.getDrawable(resources, R.drawable.coins, null)
        val imageCards =
            ResourcesCompat.getDrawable(resources, R.drawable.credit_card, null)

        val costsColor = ResourcesCompat.getDrawable(resources, android.R.color.holo_red_light, null)
        val profitsColor = ResourcesCompat.getDrawable(resources, R.color.teal_700, null)

        when (moneyType) {
            MoneyType.CASH -> recordViewHolder.moneyTypeText.setImageDrawable(imageCash)
            MoneyType.CARDS -> recordViewHolder.moneyTypeText.setImageDrawable(imageCards)
        }

        when (recordType) {
            RecordType.COSTS -> recordViewHolder.layout.background = costsColor
            RecordType.PROFITS -> recordViewHolder.layout.background = profitsColor
        }

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