package com.example.balance.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentHomeBinding
import com.example.balance.presentation.HomeViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.ui.recycler_view.BalanceListAdapter
import com.example.balance.ui.recycler_view.RecentRecordListAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var mBinding: FragmentHomeBinding? = null
    private lateinit var mNavController: NavController

    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var balanceAdapter: BalanceListAdapter
    private lateinit var recordListAdapter: RecentRecordListAdapter

    private val mViewModel by getViewModel {
        HomeViewModel(
            recordRepository = BalanceApp.recordRepository,
            datastore = BalanceApp.dataStore
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        mBinding = binding
        homeRecyclerView = binding.homeRecyclerView
        mNavController = findNavController(requireActivity(), R.id.nav_host_fragment)

        balanceAdapter = BalanceListAdapter()
        recordListAdapter = RecentRecordListAdapter()

        binding.floatingButtonCreateNewRecord.setOnClickListener { onAddRecordClick() }

        mViewModel.allRecords.observe(viewLifecycleOwner, { records ->
            records?.let { recordListAdapter.submitList(it) }
        })

        mViewModel.sumCash.observe(viewLifecycleOwner, {
            balanceAdapter.updateSumCash(it ?: 0)
            balanceAdapter.notifyItemChanged(0)
        })

        mViewModel.sumCards.observe(viewLifecycleOwner, {
            balanceAdapter.updateSumCards(it ?: 0)
        })

        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        val concatAdapter = ConcatAdapter(balanceAdapter, recordListAdapter)
        homeRecyclerView.adapter = concatAdapter
        homeRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun onAddRecordClick() {
        mNavController.navigate(R.id.recordCreationFragment)
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}