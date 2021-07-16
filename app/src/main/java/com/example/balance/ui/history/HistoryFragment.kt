package com.example.balance.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import com.example.balance.databinding.FragmentHistoryBinding
import com.example.balance.presentation.HistoryViewModel
import com.example.balance.presentation.RecyclerViewAdapter

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private var mBinding: FragmentHistoryBinding? = null
    private lateinit var mViewModel: HistoryViewModel
    private lateinit var navController: NavController
    private lateinit var historyRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)
        mBinding = binding
        historyRecyclerView = binding.historyRecyclerView
        mViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        historyRecyclerView.layoutManager = LinearLayoutManager(context)
        historyRecyclerView.adapter = RecyclerViewAdapter(mViewModel.getHistoryContent())
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}