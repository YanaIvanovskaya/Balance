package com.example.balance.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.balance.R
import com.example.balance.databinding.FragmentSettingsBinding

class SettingsFragment: Fragment(R.layout.fragment_settings) {

    private var mBinding: FragmentSettingsBinding? = null
    private lateinit var mNavController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        initViews()
        return binding.root
    }

    private fun initViews() {
        mBinding?.viewMyTemplates?.setOnClickListener {
            mNavController.navigate(R.id.templatesFragment)
        }
    }

}