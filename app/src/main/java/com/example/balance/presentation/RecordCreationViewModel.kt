package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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

class RecordCreationViewModel : ViewModel() {

    var sumOfRecord: Int = 0
    var isCosts: Boolean = true
    var isCash: Boolean = true

    val state = MutableLiveData(RecordCreationState.default())

    private fun saveSumRecordState(newSumRecord: String) {
        state.value = state.value?.copy(
            sumRecord  = newSumRecord,
            canSave = newSumRecord.isNotEmpty()
        )
    }

    fun onChangeSum(sumRecord: String) = saveSumRecordState(sumRecord)

    fun onChangeKindOperation() {

    }



}
