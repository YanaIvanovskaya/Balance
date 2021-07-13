package com.example.balance.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.util.*

class RecordCreationViewModel : ViewModel() {
    var sumOfRecord: Int = 0
    var isCosts: Boolean = true
    var isCash: Boolean = true

    init {
        Log.i("RecordCreationViewModel","View model created")
    }
}



data class Record(val id: Int) {
    val date: Date = Date()
    var sumOfMoney: Int = 0
    var isCosts: Boolean = true
    var isCash: Boolean = true



}