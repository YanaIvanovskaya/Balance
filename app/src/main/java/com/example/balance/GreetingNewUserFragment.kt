package com.example.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.balance.databinding.FragmentGreetingNewUserBinding

class GreetingNewUserFragment : Fragment(R.layout.fragment_greeting_new_user) {

    private var mBinding: FragmentGreetingNewUserBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGreetingNewUserBinding.inflate(inflater, container, false)
        mBinding = binding

        val navController = NavHostFragment.findNavController(this)

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
