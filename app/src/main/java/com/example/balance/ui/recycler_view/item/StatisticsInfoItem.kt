package com.example.balance.ui.recycler_view.item

import androidx.recyclerview.widget.RecyclerView
import com.example.balance.formatAsSum
import com.example.balance.ui.recycler_view.ViewHolderFactory

class StatCostsInfoItem(
    private val sumGeneralCosts: Int,
    private val sumAvgMonthlyCosts: Int,
    private val amountMonthlyPurchases: Int
) : Item {

    override fun getItemViewType(): Int = Item.COSTS_STAT_INFO_ITEM_TYPE

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val statViewHolder = viewHolder as ViewHolderFactory.CostsStatInfoViewHolder
        statViewHolder.apply {
            sumGeneralCostsText.text = sumGeneralCosts.formatAsSum() + " ₽"
            sumAvgMonthlyCostsText.text = sumAvgMonthlyCosts.formatAsSum() + " ₽"
            amountMonthlyPurchasesText.text = amountMonthlyPurchases.formatAsSum()
        }
    }

}

class StatProfitInfoItem(
    private val sumGeneralProfit: Int,
    private val sumAvgMonthlyProfit: Int,
    private val sumAvgMonthlyBalance: Int
) : Item {

    override fun getItemViewType(): Int = Item.PROFIT_STAT_INFO_ITEM_TYPE

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val statViewHolder = viewHolder as ViewHolderFactory.ProfitStatInfoViewHolder
        statViewHolder.apply {
            sumGeneralProfitText.text = sumGeneralProfit.formatAsSum() + " ₽"
            sumAvgMonthlyProfitText.text = sumAvgMonthlyProfit.formatAsSum() + " ₽"
            sumAvgMonthlyBalanceText.text = sumAvgMonthlyBalance.formatAsSum() + " ₽"
        }
    }

}