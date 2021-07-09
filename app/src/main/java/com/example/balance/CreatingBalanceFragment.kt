package com.example.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.balance.databinding.CreatingBalanceFragmentBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class CreatingBalanceFragment  : Fragment(R.layout.creating_balance_fragment) {

    private var mBinding: CreatingBalanceFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = CreatingBalanceFragmentBinding.inflate(inflater,container,false)
        mBinding = binding

        val navController = NavHostFragment.findNavController(this)

        binding.buttonStartUse.setOnClickListener {
            navController.navigate(R.id.homeFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}
