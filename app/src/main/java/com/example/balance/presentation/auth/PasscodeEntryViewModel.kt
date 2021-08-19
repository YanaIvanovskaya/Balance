package com.example.balance.presentation.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.BalanceRepository
import com.example.balance.data.UserDataStore
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.template.TemplateRepository
import com.example.balance.presentation.PasscodeScreenType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

data class PasscodeEntryState(
    val passcode: String,
    val canComplete: Boolean,
    val isMatches: Boolean,
    val screenType: PasscodeScreenType
) {

    companion object {
        fun default() = PasscodeEntryState(
            passcode = "",

            // для входа в приложение без ввода пароля true
            canComplete = false,
            isMatches = false,

            screenType = PasscodeScreenType.ONBOARDING
        )
    }

}

class PasscodeEntryViewModel(
    private val dataStore: UserDataStore,
    private val recordRepository: RecordRepository,
    private val templateRepository: TemplateRepository,
    private val balanceRepository: BalanceRepository,
    screenType: PasscodeScreenType
) : ViewModel() {

    val state = MutableLiveData(PasscodeEntryState.default())
    private lateinit var savedPasscode: String

    init {
        state.value = state.value?.copy(screenType = screenType)
        viewModelScope.launch(Dispatchers.IO) {
            savedPasscode = dataStore.passcode.first() ?: ""
        }
    }

    companion object {
        const val PASSCODE_LENGTH = 5
    }

    private fun savePasscodeState(newPasscode: String) {
        if (newPasscode.length <= PASSCODE_LENGTH) {
            state.value = state.value?.copy(
                passcode = newPasscode,
                canComplete = newPasscode.length == PASSCODE_LENGTH
            )
        }
    }

    fun onNumberClick(number: String) {
        val oldPasscode = state.value?.passcode ?: ""
        val newPasscode = oldPasscode + number
        savePasscodeState(newPasscode)
        checkPasscodeMatches(newPasscode)
    }

    private fun checkPasscodeMatches(passcode: String) {
        val isPasscodeMatch = state.value?.screenType == PasscodeScreenType.AUTH
                && state.value?.canComplete == true
                && savedPasscode == passcode
        if (isPasscodeMatch) {
            state.value = state.value?.copy(
                isMatches = true
            )
        }
    }

    fun onSavePasscode() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.savePasscode(state.value?.passcode.orEmpty())
        }
    }

    fun onClickClear() {
        val passcode = state.value?.passcode.orEmpty()
        val newPasscode = if (passcode.isEmpty())
            passcode
        else
            passcode.dropLast(1)
        savePasscodeState(newPasscode)
    }

    fun onAccessRecovery(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val job = launch {
                recordRepository.deleteAll()
                templateRepository.deleteAll()
            }
            job.join()
            val balanceCards = async { dataStore.balanceCards.first() }
            val balanceCash = async { dataStore.balanceCash.first() }
            dataStore.saveSumCards(balanceCards.await())
            dataStore.saveSumCash(balanceCash.await())
            balanceRepository.loadBalance()
            withContext(Dispatchers.Main) {
                callback()
            }
        }

    }

}



