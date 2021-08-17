package com.example.balance.presentation.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.BalanceRepository
import com.example.balance.data.UserDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

class BalanceCreationViewModel(
    private val dataStore: UserDataStore,
    private val balanceRepository: BalanceRepository
) : ViewModel() {

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
        if (sumCash != state.value?.sumCash || sumCards != state.value?.sumCards) {
            saveSumStates(sumCash, sumCards)
        }
    }

    fun onSaveBalance() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.saveSumCash(Integer.parseInt(state.value?.sumCash ?: "0"))
            dataStore.saveSumCards(Integer.parseInt(state.value?.sumCards ?: "0"))
            balanceRepository.loadBalance()
        }
    }

}