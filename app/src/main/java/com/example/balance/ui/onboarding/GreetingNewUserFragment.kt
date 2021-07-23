package com.example.balance.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.balance.R
import com.example.balance.databinding.FragmentGreetingNewUserBinding

class GreetingNewUserFragment : Fragment(R.layout.fragment_greeting_new_user) {

    private var mBinding: FragmentGreetingNewUserBinding? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGreetingNewUserBinding.inflate(inflater, container, false)
        mBinding = binding

        navController = findNavController() //NavHostFragment.findNavController(this)

        binding.buttonStartNext.setOnClickListener {
            navController.navigate(R.id.passcodeCreationFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}
