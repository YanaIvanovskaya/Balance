package com.example.balance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.ViewHolderFactory.create

interface RowType {

    companion object {
        const val BALANCE_ROW_TYPE: Int = 0
        const val RECORD_ROW_TYPE: Int = 1
    }

    fun getItemViewType(): Int

    fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?)
}

class BalanceRowType(
    private val balance: Int,
    private val sumCash: Int,
    private val sumCards: Int
) : RowType {

    override fun getItemViewType(): Int {
        return RowType.BALANCE_ROW_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val balanceViewHolder: ViewHolderFactory.BalanceViewHolder =
            viewHolder as ViewHolderFactory.BalanceViewHolder

        balanceViewHolder.balanceSum.text = balance.toString()
        balanceViewHolder.cashSum.text = sumCash.toString()
        balanceViewHolder.cardsSum.text = sumCards.toString()
    }

}

class RecordRowType(
    private val category: String,
    private val sumRecord: Int,
    private val date: String,
    private val isCosts: Boolean
) : RowType {

    override fun getItemViewType(): Int {
        return RowType.RECORD_ROW_TYPE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?) {
        val recordViewHolder: ViewHolderFactory.RecordViewHolder =
            viewHolder as ViewHolderFactory.RecordViewHolder

        recordViewHolder.categoryRecord.text = category
        recordViewHolder.sumRecord.text = sumRecord.toString()
        recordViewHolder.dateRecord.text = date

//        val costsBackground = ResourcesCompat.getDrawable(BalanceApp().resources,R.color.purple_200,null)
//        val profitsBackground = ResourcesCompat.getDrawable(BalanceApp().resources,R.color.teal_700,null)
//
//        if (isCosts) {
//            recordViewHolder.layout.background = costsBackground
//        } else {
//            recordViewHolder.layout.background = profitsBackground
//        }
    }

}

object ViewHolderFactory {

    fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RowType.BALANCE_ROW_TYPE -> {
                val balanceTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_type_balance, parent, false)
                BalanceViewHolder(balanceTypeView)
            }
            else -> {
                val recordTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_type_record, parent, false)
                RecordViewHolder(recordTypeView)
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
}

class HomeAdapter(
    private val dataSet: List<RowType>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        return dataSet[position].getItemViewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return create(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        dataSet[position].onBindViewHolder(holder)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}

