package com.example.balance.ui.recycler_view.item

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.RecordType
import com.example.balance.toUpperFirst
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
        recordViewHolder.categoryText.text = category.toUpperFirst()

        val resources = viewHolder.itemView.resources

        val imageCash =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_cash, null)
        val imageCards =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_card, null)

        val costsColor =
            ResourcesCompat.getColor(resources, R.color.red_300, null)
        val profitsColor = ResourcesCompat.getColor(resources, R.color.green_300, null)

        when (moneyType) {
            MoneyType.CASH -> recordViewHolder.moneyTypeText.setImageDrawable(imageCash)
            MoneyType.CARDS -> recordViewHolder.moneyTypeText.setImageDrawable(imageCards)
        }


        val prefix = when (recordType) {
            RecordType.COSTS -> {
                recordViewHolder.sumText.setTextColor(costsColor)
                "- "
            }
            else -> {
                recordViewHolder.sumText.setTextColor(profitsColor)
                "+ "
            }
        }
        val sumOfMoney = "$prefix $sumMoney ₽"
        recordViewHolder.sumText.text = sumOfMoney

        recordViewHolder.imageImportant.isVisible = isImportant

        if (comment.isNotEmpty()) {
            recordViewHolder.buttonShowComment.visibility = View.VISIBLE
            recordViewHolder.comment.text = comment
            recordViewHolder.layout.setOnClickListener {
                when (recordViewHolder.comment.visibility) {
                    View.VISIBLE -> recordViewHolder.comment.visibility = View.GONE
                    else -> recordViewHolder.comment.visibility = View.VISIBLE
                }
            }
        }
    }

}
