package com.example.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.balance.databinding.FragmentBottomNavigationBinding

class BottomNavigationFragment : Fragment(R.layout.fragment_bottom_navigation) {

    private var mBinding: FragmentBottomNavigationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomNavigationBinding.inflate(inflater, container, false)
        mBinding = binding

        val nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_bottom_fragment) as NavHostFragment
        val navController = nestedNavHostFragment.navController

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    navController.navigate(R.id.homeFragment)
                    println("HOME")
                }
                R.id.action_history -> {
                    navController.navigate(R.id.historyFragment)
                    println("HISTORY")
                }
                R.id.action_statistics -> {
                    navController.navigate(R.id.statisticsFragment)
                    println("STATISTICS")
                }
                R.id.action_settings -> {
                    navController.navigate(R.id.settingsFragment)
                    println("SETTINGS")
                }
            }
            return@setOnItemSelectedListener true
        }
        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}