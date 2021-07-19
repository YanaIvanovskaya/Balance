package com.example.balance.ui.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.Item

object ViewHolderFactory {

    fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
//            Item.BALANCE_ROW_TYPE -> {
//                val balanceTypeView: View = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.row_type_balance, parent, false)
//                BalanceViewHolder(balanceTypeView)
//            }
//            Item.RECORD_ROW_TYPE -> {
//                val recordTypeView: View = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.row_type_record, parent, false)
//                RecordViewHolder(recordTypeView)
//            }
            else -> {
                val dateTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_type_date, parent, false)
                DateViewHolder(dateTypeView)
            }
        }
    }

    class BalanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val balanceSum: TextView = itemView.findViewById(R.id.balance_sum_balance)
        val cashSum: TextView = itemView.findViewById(R.id.balance_sum_cash)
        val cardsSum: TextView = itemView.findViewById(R.id.balance_sum_cards)
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryRecord: TextView = itemView.findViewById(R.id.category)
        val sumRecord: TextView = itemView.findViewById(R.id.sum)
        val dateRecord: TextView = itemView.findViewById(R.id.date)
        val layout: ConstraintLayout = itemView.findViewById(R.id.record_layout)
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
    }
}