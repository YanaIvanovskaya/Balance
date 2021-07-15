package com.example.balance.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.databinding.FragmentHomeBinding
import com.example.balance.presentation.HomeViewModel
import com.example.balance.HomeAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var mBinding: FragmentHomeBinding? = null
    private lateinit var mViewModel: HomeViewModel
    private lateinit var navController: NavController
    private lateinit var homeRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        mBinding = binding
        homeRecyclerView = binding.homeRecyclerView
        mViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        navController = findNavController(requireActivity(), R.id.nav_host_fragment)

        binding.floatingButtonCreateNewRecord.setOnClickListener { onAddRecordClick() }
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        homeRecyclerView.layoutManager = LinearLayoutManager(context)
        homeRecyclerView.adapter = HomeAdapter(mViewModel.getHomeContent())
    }

    private fun onAddRecordClick() {
        navController.navigate(R.id.recordCreationFragment)
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}



