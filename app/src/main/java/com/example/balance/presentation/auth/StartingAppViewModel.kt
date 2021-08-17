package com.example.balance.presentation.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.BalanceApp
import com.example.balance.Event
import com.example.balance.data.BalanceRepository
import com.example.balance.data.UserDataStore
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class StartingAppViewModel(
    private val dataStore: UserDataStore,
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    val events = MutableLiveData<Event<Boolean>>()

    private fun fillDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            var date = LocalDate.of(2021, 1, 1)
            for (i in 1..200) {
                val newDate = date.plusDays((1..3).random().toLong())
                date = newDate
                for (n in 1..(1..3).random()) {
                    val recordType =
                        if ((0..1).random() == 1) RecordType.PROFITS else RecordType.COSTS
                    val record = Record(
                        day = newDate.dayOfMonth,
                        month = newDate.month.value,
                        year = newDate.year,
                        weekDay = newDate.dayOfWeek.value,
                        isImportant = (0..1).random() == 1,
                        sumOfMoney = (1..100).random(),
                        recordType = recordType,
                        moneyType = if ((0..1).random() == 1) MoneyType.CASH else MoneyType.CARDS,
                        categoryId = if (recordType == RecordType.PROFITS) (3..4).random() else (1..2).random(),
                        comment = ""
                    )
                    BalanceApp.recordRepository.insert(record)
                }
            }
        }
    }

    init {
        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                fillDatabase()
//            }
            val passcode = withContext(Dispatchers.IO) {
                dataStore.passcode.first()
            }
            val isNewUser = passcode.isNullOrEmpty()
            if (!isNewUser) {
                balanceRepository.loadBalance()
            }
            events.value = Event(isNewUser)
        }
    }

}