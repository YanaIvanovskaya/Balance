package com.example.balance.ui.recycler_view.item

import androidx.recyclerview.widget.RecyclerView
import com.example.balance.ui.recycler_view.ViewHolderFactory

class BalanceItem(
    private var sumCash: String,
    private var sumCards: String
) : Item {

    val balance: String
        get() {
            return (Integer.parseInt(sumCash) + Integer.parseInt(sumCards)).toString()
        }

    override fun getItemViewType(): Int {
        return Item.BALANCE_ITEM_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val balanceViewHolder = viewHolder as ViewHolderFactory.BalanceViewHolder

        balanceViewHolder.cashSum.text = sumCash
        balanceViewHolder.cardsSum.text = sumCards
        balanceViewHolder.balanceSum.text = balance
    }

    fun update(cash: String, cards: String) {
        sumCash = cash
        sumCards = cards
    }

}