package com.example.balance.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class StatisticsState(
    val yearsOfUse: List<Int>,
    val entriesCommonChart: List<AppBarEntry>
) {

    companion object {
        fun default() = StatisticsState(
            yearsOfUse = listOf(),
            entriesCommonChart = mutableListOf()
        )
    }

}

class StatisticsViewModel(val recordRepository: RecordRepository) : ViewModel() {

    val state = MutableLiveData(StatisticsState.default())

    init {
        viewModelScope.launch {
            val yearsOfUse =
                withContext(Dispatchers.IO) { recordRepository.getYearsOfUse().first() }

            state.value = state.value?.copy(
                yearsOfUse = yearsOfUse
            )
            val entriesCommonChart = getEntriesForCommonChart()
            state.value = state.value?.copy(
                entriesCommonChart = entriesCommonChart
            )
        }

        randomBars()
            .onEach { state.value = state.value?.copy(entriesCommonChart = it) }
            .launchIn(viewModelScope)
    }

    private suspend fun getEntriesForCommonChart(): List<AppBarEntry> {
        return withContext(Dispatchers.IO) {
            val entries = mutableListOf<BarEntry>()
            val yearsOfUse = state.value?.yearsOfUse
            if (!yearsOfUse.isNullOrEmpty()) {
                var counter = 1
                for (year in yearsOfUse) {
                    for (month in Record.months.values) {
                        val profitValue = recordRepository.getMonthlyAmount(
                            recordType = RecordType.PROFITS,
                            monthName = month,
                            year = year
                        ).first() ?: 0
                        val costsValue = recordRepository.getMonthlyAmount(
                            recordType = RecordType.COSTS,
                            monthName = month,
                            year = year
                        ).first() ?: 0
                        val barEntry = BarEntry(
                            counter.toFloat(),
                            floatArrayOf(
                                costsValue.toFloat() * -1,
                                profitValue.toFloat()
                            )
//                            floatArrayOf((-(0..9).random().toFloat()), (0..9).random().toFloat())
//                            floatArrayOf( costsValue.toFloat(),profitValue.toFloat() )
                        )

                        Log.d("BAR_X", "${barEntry.x}")
                        Log.d("BAR_yVALS", "${barEntry.yVals.joinToString()}")

                        entries.add(barEntry)
                        counter++
                    }
                }
            }
            entries.take(1)
            val values = mutableListOf<AppBarEntry>()
            for (i in 1..12) {
                values.add(
                    AppBarEntry(
                        i.toFloat(),
                        floatArrayOf(
                            -(1000..10000).random().toFloat(),
                            (1000..10000).random().toFloat()
                        )
                    )
                )
            }
            values
        }
    }
}

class AppBarEntry(
    val x: Float,
    val yVals: FloatArray
)

fun randomBars() = flow<List<AppBarEntry>> {
    while (true) {
        delay(2000)

        val values = mutableListOf<AppBarEntry>()
        for (i in 1..12) {
            values.add(
                AppBarEntry(
                    i.toFloat(),
                    floatArrayOf(
                        -(1000..10000).random().toFloat(),
                        (1000..10000).random().toFloat()
                    )
                )
            )
        }

        emit(values)
    }
}
