package com.example.balance.ui.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.MoneyType
import com.example.balance.data.Record
import com.example.balance.data.RecordType

class RecentRecordListAdapter :
    ListAdapter<Record, RecentRecordListAdapter.RecordViewHolder>(RecordsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        return RecordViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(
            date = current.date,
            sumMoney = current.sumOfMoney,
            recordType = current.recordType,
            moneyType = current.moneyType,
            category = current.category
        )
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val categoryText: TextView = itemView.findViewById(R.id.category)
        private val sumText: TextView = itemView.findViewById(R.id.sum)
        private val dateText: TextView = itemView.findViewById(R.id.date)
        private val moneyTypeText: ImageView = itemView.findViewById(R.id.image_moneyType)
        val layout: ConstraintLayout = itemView.findViewById(R.id.record_layout)

        fun bind(
            date: String,
            sumMoney: Int,
            recordType: RecordType,
            moneyType: MoneyType,
            category: String,
        ) {
            categoryText.text = category
            sumText.text = sumMoney.toString()
            dateText.text = date

            val resources = itemView.resources

            val imageCash =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_launcher_background, null)
            val imageCards =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_launcher_foreground, null)

            val costsColor = ResourcesCompat.getDrawable(resources, R.color.purple_200, null)
            val profitsColor = ResourcesCompat.getDrawable(resources, R.color.teal_700, null)

            when (moneyType) {
                MoneyType.CASH -> moneyTypeText.setImageDrawable(imageCash)
                MoneyType.CARDS -> moneyTypeText.setImageDrawable(imageCards)
            }

            when (recordType) {
                RecordType.COSTS -> layout.background = costsColor
                RecordType.PROFITS -> layout.background = profitsColor
            }

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