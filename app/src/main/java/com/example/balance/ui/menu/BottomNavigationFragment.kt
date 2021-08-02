package com.example.balance.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.balance.R
import com.example.balance.databinding.FragmentBottomNavigationBinding
import org.threeten.bp.LocalDate
import java.sql.Timestamp
import java.util.*

class BottomNavigationFragment : Fragment(R.layout.fragment_bottom_navigation) {

    private var mBinding: FragmentBottomNavigationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomNavigationBinding.inflate(inflater, container, false)
        mBinding = binding

        println(Date(System.currentTimeMillis()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomNavigation()
    }

    override fun onDestroyView() {
        mBinding = null
        println("BottomNavigationFragment destroyed")
        super.onDestroyView()
    }

    private fun initBottomNavigation() {
        val nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_bottom_fragment) as NavHostFragment
        val navController = nestedNavHostFragment.navController

        mBinding?.bottomNavigation?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> navController.navigate(R.id.homeFragment)
                R.id.action_history -> navController.navigate(R.id.historyFragment)
                R.id.action_statistics -> navController.navigate(R.id.statisticsFragment)
                R.id.action_settings -> navController.navigate(R.id.settingsFragment)
            }
            return@setOnItemSelectedListener true
        }
    }

}