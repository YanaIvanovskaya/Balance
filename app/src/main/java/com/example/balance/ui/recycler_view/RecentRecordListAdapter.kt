package com.example.balance.ui.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.Record

class RecentRecordListAdapter :
    ListAdapter<Record, RecentRecordListAdapter.RecordViewHolder>(RecordsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        return RecordViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.category, current.sumOfMoney, current.date)
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val categoryRecord: TextView = itemView.findViewById(R.id.category)
        private val sumRecord: TextView = itemView.findViewById(R.id.sum)
        private val dateRecord: TextView = itemView.findViewById(R.id.date)
        val layout: ConstraintLayout = itemView.findViewById(R.id.record_layout)

        fun bind(category: String, sumMoney: Int, date: String) {
            categoryRecord.text = category
            sumRecord.text = sumMoney.toString()
            dateRecord.text = date
        }

        companion object {
            fun create(parent: ViewGroup): RecordViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_type_record, parent, false)
                return RecordViewHolder(view)
            }
        }

    }

    class RecordsComparator : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean =
            oldItem.id == newItem.id

    }

}