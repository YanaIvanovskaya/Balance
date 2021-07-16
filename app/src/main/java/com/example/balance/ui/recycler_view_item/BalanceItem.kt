package com.example.balance.ui.recycler_view_item

import androidx.recyclerview.widget.RecyclerView
import com.example.balance.Item
import com.example.balance.presentation.ViewHolderFactory

class BalanceItem(
    private val balance: Int,
    private val sumCash: Int,
    private val sumCards: Int
) : Item {

    override fun getItemViewType(): Int {
        return Item.BALANCE_ROW_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val balanceViewHolder: ViewHolderFactory.BalanceViewHolder =
            viewHolder as ViewHolderFactory.BalanceViewHolder

        balanceViewHolder.balanceSum.text = balance.toString()
        balanceViewHolder.cashSum.text = sumCash.toString()
        balanceViewHolder.cardsSum.text = sumCards.toString()
    }

}
