package com.example.balance

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}




//    private lateinit var mNavController : NavController
//
//        val navHostFragment: NavHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

//        mNavController = navHostFragment.navController
//
//
//        val buttonNext: Button = findViewById(R.id.button_start_next)
//        buttonNext.setOnClickListener { this.onStartButtonClick() }
//
//        val buttonPrevious: Button? = findViewById(R.id.button_previous)
//        buttonPrevious?.setOnClickListener { this.onStartPreviousButtonClick() }
//
//        supportFragmentManager//    private fun onStartButtonClick() {
//        mNavController.navigate(R.id.secondFragment)
//    }
//
//    private fun onStartPreviousButtonClick() {
//        mNavController.navigate(R.id.firstFragment)
//    }