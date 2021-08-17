package com.example.balance.presentation.statistics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.StatisticsAccessor
import com.example.balance.data.record.RecordType
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GeneralChartState(
    val selectedBar: Int,
    val entries: List<BarEntry>
) {

    companion object {
        fun default() = GeneralChartState(
            selectedBar = 0,
            entries = listOf()
        )
    }

}

class GeneralChartViewModel : ViewModel() {

    val state = MutableLiveData(GeneralChartState.default())

    init {
        viewModelScope.launch {
            val entries = getEntries()
            state.value = state.value?.copy(
                entries = entries
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
                                    floatArrayOf(
                                        costsValue.toFloat() * -1,
                                        profitValue.toFloat()
                                    ),
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