//package com.example.balance.presentation
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.example.balance.BalanceApp
//import com.example.balance.data.UserDataStore
//import kotlinx.coroutines.flow.launchIn
//import kotlinx.coroutines.flow.onEach
//import kotlinx.coroutines.launch
//
//data class PasscodeCreationState(
//    val passcode: String,
//    val canComplete: Boolean
//) {
//
//    companion object {
//        fun default() = PasscodeCreationState(
//            passcode = "",
//            canComplete = false
//        )
//    }
//
//}
//
//class PasscodeCreationViewModel(
//    private val dataStore: UserDataStore
//) : ViewModel() {
//
//    val state = MutableLiveData(PasscodeCreationState.default())
//
//    init {
//        dataStore.passcode
//            .onEach { println("new: $it") }
//            .launchIn(viewModelScope)
//
//        print("Init complete")
//    }
//
//    fun onChangePasscode(passcode: String) {
//        state.value = state.value?.copy(
//            passcode = passcode,
//            canComplete = passcode.length == PASSCODE_LENGTH
//        )
//    }
//
//    fun onNumberClick(number: Int) {
//        state.value = state.value?.copy()
//    }
//
//    fun onSavePasscode() {
//        viewModelScope.launch {
//            dataStore.savePasscode(state.value?.passcode.orEmpty())
//            println("PASSCODE SAVED SUCCESSFULLY -> ${dataStore.passcode}")
//        }
//    }
//
//    fun onClickClear() {
//        val passcode = state.value?.passcode.orEmpty()
//        val newPasscode = when (passcode.isEmpty()) {
//            true -> passcode
//            false -> passcode.dropLast(1)
//        }
//
//        state.value = state.value?.copy(
//            passcode = newPasscode,
//            canComplete = passcode.length == PASSCODE_LENGTH
//        )
//    }
//
//    companion object {
//        private const val PASSCODE_LENGTH = 5
//    }
//
//}
//
//class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
//        PasscodeCreationViewModel(
//            dataStore = BalanceApp.dataStore
//        ) as T
//
//}
