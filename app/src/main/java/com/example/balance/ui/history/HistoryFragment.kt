package com.example.balance.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentHistoryBinding
import com.example.balance.presentation.HistoryViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.ui.recycler_view.RecentRecordListAdapter

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private var mBinding: FragmentHistoryBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var recordListAdapter: RecentRecordListAdapter
    private lateinit var historyRecyclerView: RecyclerView

    private val mViewModel by getViewModel {
        HistoryViewModel(
            repository = BalanceApp.recordRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)
        mBinding = binding
        historyRecyclerView = binding.historyRecyclerView
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        recordListAdapter = RecentRecordListAdapter()
        mViewModel.allRecords.observe(viewLifecycleOwner, { records ->
            records?.let { recordListAdapter.submitList(it) }
        })

        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        historyRecyclerView.layoutManager = LinearLayoutManager(context)
        historyRecyclerView.adapter = recordListAdapter
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}