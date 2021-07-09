package com.example.balance

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.balance.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setOnItemSelectedListener{ item ->
            when (item.itemId) {
            R.id.action_home -> { navController.navigate(R.id.homeFragment) }
            R.id.action_history -> { navController.navigate(R.id.historyFragment) }
            R.id.action_statistics -> { navController.navigate(R.id.statisticsFragment) }
            R.id.action_settings -> { navController.navigate(R.id.settingsFragment) }
        }
            return@setOnItemSelectedListener true
        }
    }
}