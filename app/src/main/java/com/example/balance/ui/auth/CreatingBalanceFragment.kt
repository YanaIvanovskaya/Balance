package com.example.balance.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.balance.R
import com.example.balance.databinding.FragmentCreatingBalanceBinding

class CreatingBalanceFragment : Fragment(R.layout.fragment_creating_balance) {

    private var mBinding: FragmentCreatingBalanceBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreatingBalanceBinding.inflate(inflater, container, false)
        mBinding = binding
        binding.buttonStartUse.setOnClickListener {
            findNavController().navigate(R.id.action_creatingBalanceFragment_to_bottomNavigationFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}
