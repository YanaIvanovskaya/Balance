package com.example.balance.ui.recycler_view.item

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.RecordType
import com.example.balance.formatAsSum
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

    override fun getItemViewType(): Int = Item.RECORD_ITEM_TYPE

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val recordViewHolder = viewHolder as ViewHolderFactory.RecordViewHolder
        val resources = viewHolder.itemView.resources
        val imageCash =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_cash, null)
        val imageCards =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_card, null)
        val costsColor =
            ResourcesCompat.getColor(resources, R.color.red_300, null)
        val profitsColor = ResourcesCompat.getColor(resources, R.color.green_300, null)
        val imageDownArrow =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_down_arrow, null)
        val imageUpArrow =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_up_arrow, null)

        recordViewHolder.apply {
            timeText.text = time
            categoryText.text = category.toUpperFirst()
            when (moneyType) {
                MoneyType.CASH -> moneyTypeText.setImageDrawable(imageCash)
                else -> moneyTypeText.setImageDrawable(imageCards)
            }
            val prefix = when (recordType) {
                RecordType.COSTS -> {
                    sumText.setTextColor(costsColor)
                    "- "
                }
                else -> {
                    sumText.setTextColor(profitsColor)
                    "+ "
                }
            }
            val sumOfMoney = "$prefix ${sumMoney.formatAsSum()} ₽"
            sumText.text = sumOfMoney
            imageImportant.isVisible = isImportant

            if (comment.isNotEmpty()) {
                buttonShowComment.visibility = View.VISIBLE
                commentText.text = comment
                layout.setOnClickListener {
                    when (recordViewHolder.commentText.visibility) {
                        View.VISIBLE -> {
                            commentText.visibility = View.GONE
                            buttonShowComment.background = imageDownArrow
                        }
                        else -> {
                            commentText.visibility = View.VISIBLE
                            buttonShowComment.background = imageUpArrow
                        }
                    }
                }
            }
        }
    }

}
