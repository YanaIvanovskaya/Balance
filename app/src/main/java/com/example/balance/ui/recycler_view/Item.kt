package com.example.balance.ui.recycler_view

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.RecordType

sealed interface Item {

    companion object {
        const val BALANCE_ITEM_TYPE: Int = 0
        const val RECORD_ITEM_TYPE: Int = 1
        const val DATE_ITEM_TYPE: Int = 2
    }

    fun getItemViewType(): Int

    fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?)

    class DateItem(
        private val date: String,
    ) : Item {

        override fun getItemViewType(): Int {
            return DATE_ITEM_TYPE
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
            val dateViewHolder: ViewHolderFactory.DateViewHolder =
                viewHolder as ViewHolderFactory.DateViewHolder
            dateViewHolder.date.text = date
        }

    }

    class RecordItem(
        val id: Int,
        val date: String,
        val sumMoney: Int,
        val recordType: RecordType,
        val moneyType: MoneyType,
        val category: String,
        val comment: String,
        val isImportant: Boolean
    ) : Item {

        override fun getItemViewType(): Int {
            return RECORD_ITEM_TYPE
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
            val recordViewHolder = viewHolder as ViewHolderFactory.RecordViewHolder

            recordViewHolder.dateText.text = date
            recordViewHolder.sumText.text = sumMoney.toString()
            recordViewHolder.categoryText.text = category

            val resources = viewHolder.itemView.resources

            val imageCash =
                ResourcesCompat.getDrawable(resources, R.drawable.coins, null)
            val imageCards =
                ResourcesCompat.getDrawable(resources, R.drawable.credit_card, null)

            val costsColor =
                ResourcesCompat.getDrawable(resources, android.R.color.holo_red_light, null)
            val profitsColor = ResourcesCompat.getDrawable(resources, R.color.teal_700, null)

            when (moneyType) {
                MoneyType.CASH -> recordViewHolder.moneyTypeText.setImageDrawable(imageCash)
                MoneyType.CARDS -> recordViewHolder.moneyTypeText.setImageDrawable(imageCards)
            }

            when (recordType) {
                RecordType.COSTS -> recordViewHolder.layout.background = costsColor
                RecordType.PROFITS -> recordViewHolder.layout.background = profitsColor
            }

            recordViewHolder.imageImportant.isVisible = isImportant

            if (comment.isNotEmpty()) {
                recordViewHolder.buttonShowComment.visibility = View.VISIBLE
                recordViewHolder.comment.setText(comment)
            }

            recordViewHolder.buttonShowComment.setOnClickListener {
                when (recordViewHolder.comment.visibility) {
                    View.VISIBLE -> recordViewHolder.comment.visibility = View.GONE
                    else -> recordViewHolder.comment.visibility = View.VISIBLE
                }
            }

        }

    }

    class BalanceItem(
        private var sumCash: String,
        private var sumCards: String
    ) : Item {

        val balance: String
            get() {
                return (Integer.parseInt(sumCash) + Integer.parseInt(sumCards)).toString()
            }

        override fun getItemViewType(): Int {
            return BALANCE_ITEM_TYPE
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

}


