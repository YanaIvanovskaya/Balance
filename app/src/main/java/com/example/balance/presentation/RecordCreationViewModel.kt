package com.example.balance.presentation

import android.util.Log
import androidx.lifecycle.*
import com.example.balance.BalanceApp
import com.example.balance.data.Record
import com.example.balance.data.RecordRepository
import kotlinx.coroutines.launch

data class RecordCreationState(
    val sumRecord: String,
    val isCosts: Boolean,
    val isCash: Boolean,
    val canSave: Boolean
) {

    companion object {
        fun default() = RecordCreationState(
            sumRecord = "",
            isCosts = true,
            isCash = true,
            canSave = false
        )
    }

}

class RecordCreationViewModel(
    private val repository: RecordRepository
    ) : ViewModel() {

    val state = MutableLiveData(RecordCreationState.default())

    private fun saveSumRecordState(newSumRecord: String) {
        state.value = state.value?.copy(
            sumRecord = newSumRecord,
            canSave = newSumRecord.isNotEmpty()
        )
    }

    fun onChangeSum(sumRecord: String) {
        saveSumRecordState(sumRecord)
    }

    fun onCostsSelected() {
        state.value = state.value?.copy(
            isCosts = true
        )
    }

    fun onProfitSelected() {
        state.value = state.value?.copy(
            isCosts = false
        )
    }

    fun onCashSelected() {
        state.value = state.value?.copy(
            isCash = true
        )
    }

    fun onCardsSelected() {
        state.value = state.value?.copy(
            isCash = false
        )
    }

    fun onSaveRecord() {
       viewModelScope.launch {
           val id = repository.insert(
               Record(
                   sumOfMoney = Integer.parseInt(state.value?.sumRecord ?: ""),
                   category = "Проезд",
                   date = "12 сентября 2021"
               )
           )
           println("inside viewModelScope")
           Log.d("Id test", id.toString())
       }
        println("end of viewModelScope")
    }

}
