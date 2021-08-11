package com.example.balance.ui.recycler_view.item

import androidx.recyclerview.widget.RecyclerView
import com.example.balance.getTime
import com.example.balance.ui.recycler_view.ViewHolderFactory
import org.threeten.bp.LocalTime
import java.text.NumberFormat
import java.util.*

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

        val formatter: NumberFormat = NumberFormat.getInstance(Locale("ru", "RU"))

        val sumCash = "${formatter.format(sumCash.toInt())} ₽"
        balanceViewHolder.cashSum.text = sumCash
        val sumCard = "${formatter.format(sumCards.toInt())} ₽"
        balanceViewHolder.cardsSum.text = sumCard
        val balance = "${formatter.format(balance.toInt())} ₽"
        balanceViewHolder.balanceSum.text = balance

        val balanceTitle = "Баланс на сегодня ${getTime(LocalTime.now().toString())}"
        balanceViewHolder.balanceTitle.text = balanceTitle
    }

    fun update(cash: String, cards: String) {
        sumCash = cash
        sumCards = cards
    }

}