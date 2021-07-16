package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class BalanceCreationState(
    val sumCash: String,
    val hasEmptySumCash: Boolean,
    val sumCards: String,
    val hasEmptySumCards: Boolean,
    val canComplete: Boolean,
) {

    companion object {
        fun default() = BalanceCreationState(
            sumCash = "",
            hasEmptySumCash = true,
            sumCards = "",
            hasEmptySumCards = true,
            canComplete = false
        )
    }

}

class BalanceCreationViewModel : ViewModel() {

    val state = MutableLiveData(BalanceCreationState.default())

    companion object {
        private const val SUM_LENGTH = 9
    }

    private fun saveSumStates(sumCash: String, sumCards: String) {
        val isSumCorrect = sumCash.length in 1..SUM_LENGTH && sumCards.length in 1..SUM_LENGTH
        val isSumLengthCorrect = sumCash.length <= SUM_LENGTH && sumCards.length <= SUM_LENGTH
        if (isSumLengthCorrect) {
            state.value = state.value?.copy(
                sumCash = sumCash,
                hasEmptySumCash = sumCash.isEmpty(),
                sumCards = sumCards,
                hasEmptySumCards = sumCards.isEmpty(),
                canComplete = isSumCorrect
            )
        }
    }

    fun onChangeSum(sumCash: String, sumCards: String) {
        saveSumStates(sumCash, sumCards)
    }

}