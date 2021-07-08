package com.example.balance

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.balance.databinding.CreatingBalanceFragmentBinding
import com.example.balance.databinding.HomeFragmentBinding
import com.example.balance.databinding.PasscodeCreationFragmentBinding

class HomeFragment  : Fragment(R.layout.home_fragment) {

    private lateinit var mBinding: HomeFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = HomeFragmentBinding.inflate(layoutInflater)


//        val buttonStartUse: Button = mBinding.buttonStartUse
        val navController = NavHostFragment.findNavController(this)


//        buttonStartUse.setOnClickListener { navController.navigate(R.id.)}

    }

}