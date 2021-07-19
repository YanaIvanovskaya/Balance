package com.example.balance.ui.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R

// TODO: 19.07.2021 Sticky header
class BalanceListAdapter :
    RecyclerView.Adapter<BalanceListAdapter.BalanceViewHolder>() {

    private var balance: Int = 0
    private var sumCash: Int = 0
    private var sumCards: Int = 0

    class BalanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val balanceSum: TextView = itemView.findViewById(R.id.balance_sum_balance)
        private val cashSum: TextView = itemView.findViewById(R.id.balance_sum_cash)
        private val cardsSum: TextView = itemView.findViewById(R.id.balance_sum_cards)

        fun bind(balance: Int, sumCash: Int, sumCards: Int) {
            balanceSum.text = balance.toString()
            cashSum.text = sumCash.toString()
            cardsSum.text = sumCards.toString()
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

}