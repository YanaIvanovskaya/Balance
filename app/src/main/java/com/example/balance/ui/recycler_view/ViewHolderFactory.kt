package com.example.balance.ui.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R

object ViewHolderFactory {

    fun create(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            Item.RECORD_ITEM_TYPE -> {
                val recordTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_type_record, parent, false)
                RecordViewHolder(recordTypeView)
            }
            Item.BALANCE_ITEM_TYPE -> {
                val balanceTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_type_balance, parent, false)
                BalanceViewHolder(balanceTypeView)
            }
            else -> {
                val dateTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_type_date, parent, false)
                DateViewHolder(dateTypeView)
            }
        }
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.findViewById(R.id.category)
        val sumText: TextView = itemView.findViewById(R.id.sum)
        val dateText: TextView = itemView.findViewById(R.id.date)
        val moneyTypeText: ImageView = itemView.findViewById(R.id.image_moneyType)
        val layout: ConstraintLayout = itemView.findViewById(R.id.record_layout)
        val buttonShowComment: Button = itemView.findViewById(R.id.button_show_comment)
        val comment: MultiAutoCompleteTextView = itemView.findViewById(R.id.comment)
        val imageImportant: ImageView = itemView.findViewById(R.id.image_important)
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
    }

    class BalanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val balanceSum: TextView = itemView.findViewById(R.id.balance_sum_balance)
        val cashSum: TextView = itemView.findViewById(R.id.balance_sum_cash)
        val cardsSum: TextView = itemView.findViewById(R.id.balance_sum_cards)
    }

}