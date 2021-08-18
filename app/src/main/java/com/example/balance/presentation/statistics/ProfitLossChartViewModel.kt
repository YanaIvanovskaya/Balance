package com.example.balance.presentation.statistics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.StatisticsAccessor
import com.example.balance.data.record.RecordType
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ProfitLossChartState(
    val selectedBar: Int,
    val entries: List<BarEntry>,
    val isContentLoaded: Boolean
) {

    companion object {
        fun default() = ProfitLossChartState(
            selectedBar = 0,
            entries = listOf(),
            isContentLoaded = false
        )
    }

}

class ProfitLossChartViewModel : ViewModel() {

    val state = MutableLiveData(ProfitLossChartState.default())

    init {
        viewModelScope.launch {
            val entries = async { getEntries() }
            entries.join()
            state.value = state.value?.copy(
                entries = entries.await(),
                isContentLoaded = true
            )
        }
    }

    private suspend fun getEntries(): List<BarEntry> {
        return withContext(Dispatchers.IO) {
            val entries = mutableListOf<BarEntry>()
            val yearsOfUse = StatisticsAccessor.getListYearsOfUse()
            if (!yearsOfUse.isNullOrEmpty()) {
                var counter = 1
                yearsOfUse.forEach { year ->
                    val months = StatisticsAccessor.getListMonthsInYear(year)
                    months.forEach { month ->
                        val profitValue = StatisticsAccessor.getMonthlySumByRecordType(
                            recordType = RecordType.PROFITS,
                            month = month,
                            year = year
                        )
                        val costsValue = StatisticsAccessor.getMonthlySumByRecordType(
                            recordType = RecordType.COSTS,
                            month = month,
                            year = year
                        )
                        if (profitValue != 0 || costsValue != 0) {
                            entries.add(
                                BarEntry(
                                    counter.toFloat(),
                                    (profitValue - costsValue).toFloat(),
                                    listOf(month, year)
                                )
                            )
                        }
                        counter++
                    }
                }
            }
            entries
        }
    }


}