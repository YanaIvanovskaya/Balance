package com.example.balance.ui.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.ui.recycler_view.item.Item
import com.github.mikephil.charting.charts.BarChart

object ViewHolderFactory {

    fun create(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            Item.RECORD_ITEM_TYPE -> {
                val recordTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_record, parent, false)
                RecordViewHolder(recordTypeView)
            }
            Item.BALANCE_ITEM_TYPE -> {
                val balanceTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_balance, parent, false)
                BalanceViewHolder(balanceTypeView)
            }
            Item.TEMPLATE_ITEM_TYPE -> {
                val templateTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_template, parent, false)
                TemplateViewHolder(templateTypeView)
            }
            Item.CATEGORY_ITEM_TYPE -> {
                val categoryTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category, parent, false)
                CategoryViewHolder(categoryTypeView)
            }
            Item.CATEGORY_CHART_ITEM_TYPE -> {
                val chartTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category_chart, parent, false)
                CategoryChartViewHolder(chartTypeView)
            }
            Item.COSTS_STAT_INFO_ITEM_TYPE -> {
                val costsStatTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_costs_stat_info, parent, false)
                CostsStatInfoViewHolder(costsStatTypeView)
            }
            Item.PROFIT_STAT_INFO_ITEM_TYPE -> {
                val profitStatTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_profit_stat_info, parent, false)
                ProfitStatInfoViewHolder(profitStatTypeView)
            }
            Item.NO_ITEMS_ITEM_TYPE -> {
                val noItemsTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_no_items, parent, false)
                NoItemsItemViewHolder(noItemsTypeView)
            }
            else -> {
                val dateTypeView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date, parent, false)
                DateViewHolder(dateTypeView)
            }
        }
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.findViewById(R.id.category)
        val sumText: TextView = itemView.findViewById(R.id.sum)
        val dateText: TextView = itemView.findViewById(R.id.date)
        val moneyTypeText: ImageView = itemView.findViewById(R.id.image_MoneyType)
        val layout: ConstraintLayout = itemView.findViewById(R.id.record_layout)
        val buttonShowComment: ImageButton = itemView.findViewById(R.id.button_show_comment)
        val comment: TextView = itemView.findViewById(R.id.comment)
        val imageImportant: ImageView = itemView.findViewById(R.id.image_important)
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.usage_template)
    }

    class BalanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val balanceSum: TextView = itemView.findViewById(R.id.balance_sum_balance)
        val cashSum: TextView = itemView.findViewById(R.id.balance_sum_cash)
        val cardsSum: TextView = itemView.findViewById(R.id.balance_sum_cards)
        val balanceTitle: TextView = itemView.findViewById(R.id.balance_title)
    }

    class TemplateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTemplateText: TextView = itemView.findViewById(R.id.name_template)
        val categoryText: TextView = itemView.findViewById(R.id.category_template)
        val sumText: TextView = itemView.findViewById(R.id.sum_template)
        val moneyTypeText: ImageView = itemView.findViewById(R.id.image_money_type_template)
        val layout: ConstraintLayout = itemView.findViewById(R.id.template_layout)
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameCategoryText: TextView = itemView.findViewById(R.id.name_category)
        val dateCreationText: TextView = itemView.findViewById(R.id.date_creation_category)
        val layout: ConstraintLayout = itemView.findViewById(R.id.category_layout)
    }

    class CategoryChartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameCategoryText: TextView = itemView.findViewById(R.id.title_name_category)
        val sumAvgMonthly: TextView = itemView.findViewById(R.id.sum_avg_monthly)
        val categoryChart: BarChart = itemView.findViewById(R.id.category_chart)
    }

    class CostsStatInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sumGeneralCosts: TextView = itemView.findViewById(R.id.sum_general_costs)
        val sumAvgMonthlyCosts: TextView = itemView.findViewById(R.id.sum_avg_monthly_costs)
        val amountMonthlyPurchases: TextView = itemView.findViewById(R.id.amount_purchases)
    }

    class ProfitStatInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sumGeneralProfit: TextView = itemView.findViewById(R.id.sum_general_profits)
        val sumAvgMonthlyProfit: TextView = itemView.findViewById(R.id.sum_avg_monthly_profit)
        val sumAvgMonthlyBalance: TextView = itemView.findViewById(R.id.amount_balance)
    }

    class NoItemsItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val image: ImageView = itemView.findViewById(R.id.image_no_items)
        val message: TextView = itemView.findViewById(R.id.text_no_items)
        val buttonAdd: TextView = itemView.findViewById(R.id.button_no_items)
    }

}