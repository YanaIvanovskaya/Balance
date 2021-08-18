package com.example.balance.presentation.statistics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.StatisticsAccessor
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


data class GeneralStatisticsState(
    val currentPagePosition: Int,
    val isContentLoaded: Boolean
) {

    companion object {
        fun default() = GeneralStatisticsState(
            currentPagePosition = 0,
            isContentLoaded = false
        )
    }

}

class GeneralStatisticsViewModel : ViewModel() {

    val state = MutableLiveData(GeneralStatisticsState.default())

    fun savePagePosition(position: Int) {
        state.value = state.value?.copy(
            currentPagePosition = position
        )
    }

}