package com.example.balance.ui.recycler_view.item

import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.RecordType
import com.example.balance.toUpperFirst
import com.example.balance.ui.recycler_view.ViewHolderFactory

class TemplateItem(
    val id: Int,
    val name: String,
    val usage: Int,
    val sumMoney: Int,
    val recordType: RecordType,
    val moneyType: MoneyType,
    val category: String
) : Item {

    override fun getItemViewType(): Int {
        return Item.TEMPLATE_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val templateViewHolder = viewHolder as ViewHolderFactory.TemplateViewHolder

        templateViewHolder.nameTemplateText.text = name.toUpperFirst()
        templateViewHolder.sumText.text = sumMoney.toString()
        templateViewHolder.categoryText.text = category.toUpperFirst()

        val resources = viewHolder.itemView.resources

        val imageCash =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_cash, null)
        val imageCards =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_card, null)

        val costsBg =
            ResourcesCompat.getDrawable(resources, R.drawable.selector_record_costs, null)
        val profitBg = ResourcesCompat.getDrawable(resources, R.drawable.selector_record_profit, null)

        when (moneyType) {
            MoneyType.CASH -> templateViewHolder.moneyTypeText.setImageDrawable(imageCash)
            MoneyType.CARDS -> templateViewHolder.moneyTypeText.setImageDrawable(imageCards)
        }

        when (recordType) {
            RecordType.COSTS -> templateViewHolder.layout.background = costsBg
            RecordType.PROFITS -> templateViewHolder.layout.background = profitBg
        }

    }

}