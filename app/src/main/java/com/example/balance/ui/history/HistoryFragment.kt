package com.example.balance.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentHistoryBinding
import com.example.balance.presentation.HistoryViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.ui.menu.BottomNavigationFragmentDirections
import com.example.balance.ui.recycler_view.HistoryRecyclerViewAdapter
import com.example.balance.ui.recycler_view.SwipeToDeleteCallback


class HistoryFragment : Fragment(R.layout.fragment_history) {

    private var mBinding: FragmentHistoryBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryRecyclerViewAdapter
    private val mViewModel by getViewModel {
        HistoryViewModel(
            recordRepository = BalanceApp.recordRepository,
            templateRepository = BalanceApp.templateRepository
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
        mNavController = findNavController(requireActivity(), R.id.nav_host_fragment)
        historyAdapter = HistoryRecyclerViewAdapter(
            onEditClickListener = { recordId, position ->
                val action = BottomNavigationFragmentDirections
                    .actionBottomNavigationFragmentToRecordEditingFragment(recordId)
                // historyAdapter.notifyItemChanged(position)
                mNavController.navigate(action)
            },
            onDeleteClickListener = { recordId, position ->
                mViewModel.removeRecord(requireContext(), recordId)
//                historyAdapter.
            })
        initRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.allRecords.observe(viewLifecycleOwner) { list ->
            historyAdapter.updateData(list.toMutableList())
            historyAdapter.notifyDataSetChanged()
        }
    }


    private fun initRecyclerView() {
        historyRecyclerView.layoutManager = LinearLayoutManager(context)
        historyRecyclerView.adapter = historyAdapter


        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                historyAdapter.removeAt(viewHolder.bindingAdapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(historyRecyclerView)
//        historyRecyclerView.addItemDecoration(HeaderItemDecoration(historyRecyclerView,isHeader = 0))
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}