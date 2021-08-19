package com.example.balance.presentation.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.BalanceApp
import com.example.balance.Event
import com.example.balance.data.BalanceRepository
import com.example.balance.data.UserDataStore
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.lang.Exception

// TODO: 19.08.2021 длина шаблона 
// TODO: 19.08.2021


class StartingAppViewModel(
    private val dataStore: UserDataStore,
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    val events = MutableLiveData<Event<Boolean>>()

    init {

        viewModelScope.launch {
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