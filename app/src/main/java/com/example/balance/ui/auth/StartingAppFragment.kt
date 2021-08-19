package com.example.balance.ui.auth

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.presentation.auth.StartingAppViewModel
import com.example.balance.presentation.getViewModel

class StartingAppFragment : Fragment(R.layout.fragment_starting_app) {

    private val mViewModel by getViewModel {
        StartingAppViewModel(
            dataStore = BalanceApp.dataStore,
            balanceRepository = BalanceApp.balanceRepository
        )
    }
    private lateinit var mNavController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mNavController = findNavController()
        mViewModel.events.observe(viewLifecycleOwner) { event ->
            event.consume { isNewUser ->
                mNavController.navigate(
                    when {
                        isNewUser -> R.id.onboarding_nav_graph
                        else -> R.id.auth_nav_graph
                    }
                )
            }
        }
    }

}


