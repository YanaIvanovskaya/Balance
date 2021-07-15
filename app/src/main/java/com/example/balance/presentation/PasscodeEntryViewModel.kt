package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.UserDataStore
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class PasscodeEntryState(
    val passcode: String,
//    val passcodeVisible: Boolean,
    val canComplete: Boolean,
    val screenType: PasscodeScreenType
) {

    companion object {
        fun default() = PasscodeEntryState(
            passcode = "",
//            passcodeVisible = false,
            canComplete = false,
            screenType = PasscodeScreenType.ONBOARDING
        )
    }

}

class PasscodeEntryViewModel(
    private val dataStore: UserDataStore
) : ViewModel() {

    val state = MutableLiveData(PasscodeEntryState.default())

    init {
        dataStore.passcode
            .onEach { println("new: $it") }
            .launchIn(viewModelScope)
        print("Init complete")
    }

    companion object {
        private const val PASSCODE_LENGTH = 5
    }

    private fun savePasscodeState (newPasscode: String) {
        if (newPasscode.length <= PASSCODE_LENGTH) {
            state.value = state.value?.copy(
                passcode = newPasscode,
//                passcodeVisible = false,
                canComplete = newPasscode.length == PASSCODE_LENGTH
            )
        }
    }

    fun onChangePasscode(passcode: String) {
        savePasscodeState(passcode)
    }

    fun onNumberClick(number: Int) {
        val oldPasscode = state.value?.passcode ?: ""
        val newPasscode = oldPasscode + number
        savePasscodeState(newPasscode)
    }

    fun onSavePasscode() {
        viewModelScope.launch {
            dataStore.savePasscode(state.value?.passcode.orEmpty())
            println("PASSCODE SAVED SUCCESSFULLY -> ${dataStore.passcode}")
        }
    }

    fun onClickClear() {
        val passcode = state.value?.passcode.orEmpty()
        val newPasscode = when (passcode.isEmpty()) {
            true -> passcode
            false -> passcode.dropLast(1)
        }
//        val newPasscode = if (passcode.isEmpty()) passcode else passcode.dropLast(1)
        savePasscodeState(newPasscode)
    }

}



