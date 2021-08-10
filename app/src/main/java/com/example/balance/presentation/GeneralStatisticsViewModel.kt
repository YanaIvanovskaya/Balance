package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.StatisticsAccessor
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.toUpperFirst
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class GeneralStatisticsState(
    val currentPagePosition: Int,
    val haveRecords: Boolean
) {

    companion object {
        fun default() = GeneralStatisticsState(
            currentPagePosition = 0,
            haveRecords = false
        )
    }

}

class GeneralStatisticsViewModel : ViewModel() {

    val state = MutableLiveData(GeneralStatisticsState.default())

    init {
        viewModelScope.launch {
            val yearsOfUse = StatisticsAccessor.getListYearsOfUse()
            if (yearsOfUse.isNotEmpty()) {
                state.value = state.value?.copy(
                    haveRecords = true
                )
            }
        }
    }

    fun savePagePosition(position: Int) {
        state.value = state.value?.copy(
            currentPagePosition = position
        )
    }

}