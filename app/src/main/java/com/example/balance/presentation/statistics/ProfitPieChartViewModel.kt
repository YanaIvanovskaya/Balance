package com.example.balance.presentation.statistics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.StatisticsAccessor
import com.example.balance.data.category.CategoryType
import com.example.balance.toUpperFirst
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ProfitPieChartState(
    val selectedBar: Int,
    val entries: List<PieEntry>,
    val maxProfitCategory: String
) {

    companion object {
        fun default() = ProfitPieChartState(
            selectedBar = 0,
            entries = listOf(),
            maxProfitCategory = ""
        )
    }

}

class ProfitPieChartViewModel : ViewModel() {

    val state = MutableLiveData(ProfitPieChartState.default())

    init {
        viewModelScope.launch {
            val entries = getEntries()
            state.value = state.value?.copy(
                entries = entries
            )
        }
    }

    private suspend fun getEntries(): List<PieEntry> {
        return withContext(Dispatchers.IO) {
            val pieEntries = mutableListOf<PieEntry>()
            val listCategoryWithSum =
                StatisticsAccessor.getListCategoryWithSum(CategoryType.CATEGORY_PROFIT)
            val maxSumCategory: Pair<String, Int>? = listCategoryWithSum.maxByOrNull { it.second }
            withContext(Dispatchers.Main) {
                state.value = state.value?.copy(maxProfitCategory = maxSumCategory?.first ?: "")
            }
            for (pair in listCategoryWithSum) {
                val value = pair.second.toFloat()
                if (value != 0f) {
                    val pieEntry = PieEntry(value, pair.first.toUpperFirst())
                    pieEntries.add(pieEntry)
                }
            }
            pieEntries
        }
    }

}