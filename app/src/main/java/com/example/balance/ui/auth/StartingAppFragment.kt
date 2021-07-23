package com.example.balance.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.BalanceApp
import com.example.balance.Event
import com.example.balance.R
import com.example.balance.data.UserDataStore
import com.example.balance.presentation.getViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StartingAppFragment : Fragment(R.layout.fragment_starting_app) {

    private val mViewModel by getViewModel {
        StartingAppViewModel(
            dataStore = BalanceApp.dataStore
        )
    }
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        mViewModel.events.observe(viewLifecycleOwner) { event ->
            event.consume { isNewUser ->
                navController.navigate(
                    when {
                        isNewUser -> R.id.onboarding_nav_graph
                        else -> R.id.auth_nav_graph
                    }
                )
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
//            dataStore.savePasscode("00000")
//            dataStore.clearBalance()

            val passcode = dataStore.passcode.first()
            val isNewUser = passcode.isNullOrEmpty()
            events.value = Event(isNewUser)
        }
    }

}
