package com.example.balance.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentHistoryBinding
import com.example.balance.presentation.HistoryViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.ui.menu.BottomNavigationFragmentDirections
import com.example.balance.ui.recycler_view.HistoryAdapter
import com.example.balance.ui.recycler_view.Item
import com.example.balance.ui.recycler_view.ItemDiffUtilCallback
import com.google.android.material.bottomsheet.BottomSheetDialog

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private var mBinding: FragmentHistoryBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
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
        historyAdapter = HistoryAdapter(
            onLongItemClickListener = { recordId, position ->
                showBottomSheetDialog(recordId,position)
                true
            })
        initRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.allHistoryRecords.observe(viewLifecycleOwner) { list ->
            historyAdapter.dataSet = list.toMutableList()
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun updateAdapter(productList: MutableList<Item>) {
        val itemDiffUtilCallback = ItemDiffUtilCallback(historyAdapter.dataSet, productList)
        val itemDiffResult = DiffUtil.calculateDiff(itemDiffUtilCallback)
        historyAdapter.dataSet = productList
        itemDiffResult.dispatchUpdatesTo(historyAdapter)
    }

    private fun initRecyclerView() {
        historyRecyclerView.layoutManager = LinearLayoutManager(context)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun showBottomSheetDialog(recordId: Int, position: Int) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.fragment_bottom_sheet)
        val pin = bottomSheetDialog.findViewById<LinearLayout>(R.id.pin_unpin_record)
        bottomSheetDialog.findViewById<TextView>(R.id.pin_unpin_record_label)?.text =
            "Закрепить на главном экране"
        val edit = bottomSheetDialog.findViewById<LinearLayout>(R.id.edit_record)
        val delete = bottomSheetDialog.findViewById<LinearLayout>(R.id.delete_record)

        edit?.setOnClickListener {
            val action = BottomNavigationFragmentDirections
                .actionBottomNavigationFragmentToRecordEditingFragment(recordId)
            bottomSheetDialog.dismiss()
            mNavController.navigate(action)
        }
        delete?.setOnClickListener {
            mViewModel.removeRecord(recordId)
            bottomSheetDialog.dismiss()
        }
        pin?.setOnClickListener {
            mViewModel.onPinClick(recordId)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}