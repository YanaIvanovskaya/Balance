package com.example.balance.ui.auth

import com.example.balance.presentation.ViewModelFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.Event
import com.example.balance.R
import com.example.balance.data.UserDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class StartingAppFragment : Fragment(R.layout.fragment_starting_app) {

    private val mViewModel by viewModels<StartingAppViewModel> { ViewModelFactory() }
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        mViewModel.events.observe(viewLifecycleOwner) { event ->
            event.consume { isNewUser ->
                navController.navigate(when {
                    isNewUser -> R.id.onboarding_nav_graph
                    else -> R.id.onboarding_nav_graph
                })
            }
        }
    }

}

class StartingAppViewModel(
    private val dataStore: UserDataStore
) : ViewModel() {

    val events = MutableLiveData<Event<Boolean>>()

    init {
        viewModelScope.launch {
            val passcode = dataStore.passcode.first()
            val isNewUser = passcode.isNullOrEmpty()
            events.value = Event(isNewUser)
        }
    }

}
