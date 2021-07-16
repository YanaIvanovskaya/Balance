package com.example.balance.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.balance.BalanceApp
import com.example.balance.data.UserDataStore

class ViewModelFactory(
    private val screenType: PasscodeScreenType
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        modelClass.getConstructor(UserDataStore::class.java)
            .newInstance(BalanceApp.dataStore)

}

inline fun <reified TViewModel : ViewModel> Fragment.getViewModel(
    crossinline constructor: () -> TViewModel
) = viewModels<TViewModel> {
    object : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return constructor() as T
        }
    }
}
