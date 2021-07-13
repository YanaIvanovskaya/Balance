package com.example.balance.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.balance.R
import com.example.balance.databinding.FragmentPasscodeCreationBinding

class PasscodeCreationFragment : Fragment(R.layout.fragment_passcode_creation) {

    private var mBinding: FragmentPasscodeCreationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPasscodeCreationBinding.inflate(inflater, container, false)
        mBinding = binding
        val navController = NavHostFragment.findNavController(this)

        binding.buttonPrevious.setOnClickListener {
            navController.navigate(R.id.greetingNewUserFragment)
        }
        binding.buttonNext.setOnClickListener {
            navController.navigate(R.id.creatingBalanceFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}
