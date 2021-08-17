package com.example.balance.ui.recycler_view.item

import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.RecordType
import com.example.balance.formatAsSum
import com.example.balance.toUpperFirst
import com.example.balance.ui.recycler_view.ViewHolderFactory

class TemplateItem(
    val id: Int,
    val name: String,
    val sumMoney: Int,
    val recordType: RecordType,
    val moneyType: MoneyType,
    val category: String
) : Item {

    override fun getItemViewType(): Int = Item.TEMPLATE_ITEM_TYPE

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val templateViewHolder = viewHolder as ViewHolderFactory.TemplateViewHolder

        val resources = viewHolder.itemView.resources
        val imageCash =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_cash, null)
        val imageCards =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_card, null)
        val costsColor =
            ResourcesCompat.getColor(resources, R.color.red_300, null)
        val profitsColor = ResourcesCompat.getColor(resources, R.color.green_300, null)

        templateViewHolder.apply {
            nameTemplateText.text = name.toUpperFirst()
            sumText.text = sumMoney.toString()
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
        }
    }

}