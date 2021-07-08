package com.example.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.balance.databinding.MainNavigationFragmentBinding


class NavigationFragment: Fragment(R.layout.main_navigation_fragment) {

    private var mBinding: MainNavigationFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = MainNavigationFragmentBinding.inflate(inflater,container,false)
        mBinding = binding

        val navController = NavHostFragment.findNavController(this)

        binding.bottomNavigation.setOnItemSelectedListener{
            item -> when (item.itemId) {
                R.id.action_home -> {navController.navigate(R.id.homeFragment)}
                R.id.action_history -> {navController.navigate(R.id.historyFragment)}
//                R.id.action_statistics -> {navController.navigate(R.id.statisticsFragment)}
//                R.id.action_settings -> {navController.navigate(R.id.settingsFragment)}
            }
            false
        }

        return binding.root
    }
}