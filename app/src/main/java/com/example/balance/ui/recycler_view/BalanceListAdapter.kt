package com.example.balance.ui.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R

class BalanceListAdapter :
    RecyclerView.Adapter<BalanceListAdapter.BalanceViewHolder>() {

    private var sumCash: String = "0"
    private var sumCards: String = "0"

    private val balance: String
        get() {
            return (Integer.parseInt(sumCash) + Integer.parseInt(sumCards)).toString()
        }

    class BalanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val balanceSum: TextView = itemView.findViewById(R.id.balance_sum_balance)
        private val cashSum: TextView = itemView.findViewById(R.id.balance_sum_cash)
        private val cardsSum: TextView = itemView.findViewById(R.id.balance_sum_cards)

        fun bind(balance: String, sumCash: String, sumCards: String) {
            balanceSum.text = balance
            cashSum.text = sumCash
            cardsSum.text = sumCards
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_type_balance, parent, false)
        return BalanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: BalanceViewHolder, position: Int) =
        holder.bind(balance, sumCash, sumCards)

    override fun getItemCount(): Int = 1

    fun updateValues(cash: String,cards: String) {
        sumCash = cash
        sumCards = cards
    }

}