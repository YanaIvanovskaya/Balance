package com.example.balance.ui.auth

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.balance.R

class StartingAppFragment : Fragment(R.layout.fragment_starting_app) {

    private val isNewUser: Boolean
        get() = (0..1).random() == 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        findNavController().navigate(R.id.auth_nav_graph)
        findNavController().navigate(R.id.onboarding_nav_graph)
//        Handler(Looper.getMainLooper()).postDelayed(
//            {
//                if (isNewUser)
//                    findNavController().navigate(R.id.auth_nav_graph)
//                else
//                    findNavController().navigate(R.id.onboarding_nav_graph)
//            }, 1000
//        )
    }

}
