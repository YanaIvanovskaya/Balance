package com.example.balance

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment


class StartingAppFragment: Fragment(R.layout.starting_app_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if (checkNewUser())
                navController.navigate(R.id.greetingNewUserFragment)
            else
                navController.navigate(R.id.passcodeEntryFragment)
        },5000)
    }
}

fun checkNewUser(): Boolean = (1..1).random() == 1

