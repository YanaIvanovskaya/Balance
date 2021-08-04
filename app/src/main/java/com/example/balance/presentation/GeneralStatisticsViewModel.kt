package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class GeneralStatisticsState(
    val yearsOfUse: List<Int>,
    val entriesCommonChart: List<BarEntry>,
    val entriesRestChart: List<BarEntry>
) {

    companion object {
        fun default() = GeneralStatisticsState(
            yearsOfUse = listOf(),
            entriesCommonChart = mutableListOf(),
            entriesRestChart = mutableListOf()
        )
    }

}

class GeneralStatisticsViewModel(
    val recordRepository: RecordRepository
) : ViewModel() {

    val state = MutableLiveData(GeneralStatisticsState.default())

    init {
        viewModelScope.launch {
            val yearsOfUse =
                withContext(Dispatchers.IO) { recordRepository.getYearsOfUse().first() }

            state.value = state.value?.copy(
                yearsOfUse = yearsOfUse
            )

            val entriesCommonChart = getEntriesForChart(ChartType.COMMON_CHART)
            val entriesRestChart = getEntriesForChart(ChartType.REST_CHART)
            state.value = state.value?.copy(
                entriesCommonChart = entriesCommonChart,
                entriesRestChart = entriesRestChart
            )
        }
    }

    enum class ChartType {
        COMMON_CHART,
        REST_CHART
    }

    private suspend fun getEntriesForChart(chartType: ChartType): List<BarEntry> {
        return withContext(Dispatchers.IO) {
            val entries = mutableListOf<BarEntry>()
            val yearsOfUse = state.value?.yearsOfUse
            if (!yearsOfUse.isNullOrEmpty()) {

                var counter = 1
                for (year in yearsOfUse) {
                    val months = recordRepository.getMonthsInYear(year).first()
                    println(months)
                    for (month in months) {
                        val profitValue = recordRepository.getMonthlyAmount(
                            recordType = RecordType.PROFITS,
                            month = month,
                            year = year
                        ).first() ?: 0
                        val costsValue = recordRepository.getMonthlyAmount(
                            recordType = RecordType.COSTS,
                            month = month,
                            year = year
                        ).first() ?: 0

                        if (profitValue != 0 || costsValue != 0) {
                            val barEntry = when (chartType) {
                                ChartType.COMMON_CHART -> {
                                    BarEntry(
                                        counter.toFloat(),
                                        floatArrayOf(
                                            costsValue.toFloat() * -1,
                                            profitValue.toFloat()
                                        ),
                                        month
                                    )
                                }
                                ChartType.REST_CHART -> {
                                    BarEntry(
                                        counter.toFloat(),
                                        (profitValue - costsValue).toFloat(),
                                        month
                                    )
                                }
                            }
                            entries.add(barEntry)
                        }
                        counter++
                    }
                }
            }
            entries
        }
    }

}