package com.example.balance.presentation.statistics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.StatisticsAccessor
import kotlinx.coroutines.launch


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