package com.example.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.delay

class StartingAppFragment: Fragment(R.layout.starting_app_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this)

        if (checkNewUser())
            navController.navigate(R.id.greetingNewUserFragment)
        else
            navController.navigate(R.id.passcodeEntryFragment)
    }
}


fun checkNewUser(): Boolean = (1..1).random() == 1

