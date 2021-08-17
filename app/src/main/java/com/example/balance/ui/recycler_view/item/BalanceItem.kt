package com.example.balance.ui.recycler_view.item

import androidx.recyclerview.widget.RecyclerView
import com.example.balance.formatAsSum
import com.example.balance.getTime
import com.example.balance.ui.recycler_view.ViewHolderFactory
import org.threeten.bp.LocalTime

class BalanceItem(
    private var sumCash: String,
    private var sumCards: String
) : Item {

    val balance: String
        get() = (Integer.parseInt(sumCash) + Integer.parseInt(sumCards)).toString()

    override fun getItemViewType(): Int = Item.BALANCE_ITEM_TYPE

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val balanceViewHolder = viewHolder as ViewHolderFactory.BalanceViewHolder
        val sumCash = "${sumCash.toInt().formatAsSum()} ₽"
        val sumCard = "${sumCards.toInt().formatAsSum()} ₽"
        val balance = "${balance.toInt().formatAsSum()} ₽"
        val balanceTitleText = "Баланс на сегодня ${getTime(LocalTime.now().toString())}"
        balanceViewHolder.apply {
            cashSum.text = sumCash
            cardsSum.text = sumCard
            balanceSum.text = balance
            balanceTitle.text = balanceTitleText
        }
    }

    fun update(cash: String, cards: String) {
        sumCash = cash
        sumCards = cards
    }

}