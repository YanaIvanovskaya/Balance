package com.example.balance.ui.recycler_view.item

import androidx.recyclerview.widget.RecyclerView
import com.example.balance.ui.recycler_view.ViewHolderFactory

class StatCostsInfoItem(
    private val sumGeneralCosts: Int,
    private val sumAvgMonthlyCosts: Int,
    private val amountMonthlyPurchases: Int
) : Item {

    override fun getItemViewType(): Int {
        return Item.COSTS_STAT_INFO_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val statViewHolder = viewHolder as ViewHolderFactory.CostsStatInfoViewHolder
        statViewHolder.sumGeneralCosts.text = sumGeneralCosts.toString()
        statViewHolder.sumAvgMonthlyCosts.text = sumAvgMonthlyCosts.toString()
        statViewHolder.amountMonthlyPurchases.text = amountMonthlyPurchases.toString()
    }

}

class StatProfitInfoItem(
    private val sumGeneralProfit: Int,
    private val sumAvgMonthlyProfit: Int,
    private val sumAvgMonthlyBalance: Int
) : Item {

    override fun getItemViewType(): Int {
        return Item.PROFIT_STAT_INFO_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val statViewHolder = viewHolder as ViewHolderFactory.ProfitStatInfoViewHolder
        statViewHolder.sumGeneralProfit.text = sumGeneralProfit.toString()
        statViewHolder.sumAvgMonthlyProfit.text = sumAvgMonthlyProfit.toString()
        statViewHolder.sumAvgMonthlyBalance.text = sumAvgMonthlyBalance.toString()
    }

}