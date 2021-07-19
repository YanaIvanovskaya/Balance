package com.example.balance.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified TViewModel : ViewModel> Fragment.getViewModel(
    crossinline constructor: () -> TViewModel
) = viewModels<TViewModel> {
    object : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return constructor() as T
        }
    }
}
