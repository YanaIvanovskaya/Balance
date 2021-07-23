package com.example.balance.ui.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.data.MoneyType
import com.example.balance.data.Record
import com.example.balance.data.RecordType

class ImportantRecordListAdapter :
    ListAdapter<Record, ImportantRecordListAdapter.RecordViewHolder>(RecordsComparator()) {

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
            category = current.category,
            comment = current.comment
        )
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val categoryText: TextView = itemView.findViewById(R.id.category)
        private val sumText: TextView = itemView.findViewById(R.id.sum)
        private val dateText: TextView = itemView.findViewById(R.id.date)
        private val moneyTypeImage: ImageView = itemView.findViewById(R.id.image_moneyType)
        private val importantImage: ImageView = itemView.findViewById(R.id.image_important)
        private val commentText: TextView = itemView.findViewById(R.id.textView_comment)
        val layout: ConstraintLayout = itemView.findViewById(R.id.constraint)

        fun bind(
            date: String,
            sumMoney: Int,
            recordType: RecordType,
            moneyType: MoneyType,
            category: String,
            comment: String
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
                MoneyType.CASH -> moneyTypeImage.setImageDrawable(imageCash)
                MoneyType.CARDS -> moneyTypeImage.setImageDrawable(imageCards)
            }

            when (recordType) {
                RecordType.COSTS -> layout.background = costsColor
                RecordType.PROFITS -> layout.background = profitsColor
            }

            importantImage.isVisible = true
            if (comment.isNotEmpty()) {
                commentText.isVisible = true
                commentText.text = comment
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