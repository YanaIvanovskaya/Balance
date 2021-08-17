package com.example.balance.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentHistoryBinding
import com.example.balance.presentation.history.HistoryState
import com.example.balance.presentation.history.HistoryViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.ui.menu.BottomNavigationFragmentDirections
import com.example.balance.ui.recycler_view.DividerItemDecoration
import com.example.balance.ui.recycler_view.adapter.HistoryAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private var mBinding: FragmentHistoryBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private val mViewModel by getViewModel {
        HistoryViewModel(
            recordRepository = BalanceApp.recordRepository,
            templateRepository = BalanceApp.templateRepository,
            categoryRepository = BalanceApp.categoryRepository
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyAdapter = HistoryAdapter(
            onLongItemClickListener = { recordId, isImportant ->
                showRecordMenu(recordId, isImportant)
                true
            })
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        initRecyclerView()
        initChips()
    }

    private fun render(state: HistoryState) {
        when (state.currentChip) {
            0 -> {
                mBinding?.chipAllHistory?.isChecked = true
                historyAdapter.dataSet = state.allRecords
            }
            1 -> {
                mBinding?.chipProfitHistory?.isChecked = true
                historyAdapter.dataSet = state.profitRecords
            }
            2 -> {
                mBinding?.chipCostsHistory?.isChecked = true
                historyAdapter.dataSet = state.costsRecords
            }
            3 -> {
                mBinding?.chipImportantHistory?.isChecked = true
                historyAdapter.dataSet = state.importantRecords
            }
        }
        historyAdapter.notifyDataSetChanged()
        mBinding?.preloaderHistory?.visibility =
            if (state.hasNoRecords || state.isContentLoaded) View.GONE
            else View.VISIBLE
    }

    private fun initRecyclerView() {
        historyRecyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        ResourcesCompat.getDrawable(resources, R.drawable.item_divider, null)
            ?.let { DividerItemDecoration(it) }
            ?.let { historyRecyclerView.addItemDecoration(it) }
        historyRecyclerView.adapter = historyAdapter
    }

    private fun initChips() {
        mBinding?.chipAllHistory?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mViewModel.saveCurrentChip(0)
            }
        }
        mBinding?.chipProfitHistory?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mViewModel.saveCurrentChip(1)
            }
        }
        mBinding?.chipCostsHistory?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mViewModel.saveCurrentChip(2)
            }
        }
        mBinding?.chipImportantHistory?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mViewModel.saveCurrentChip(3)
            }
        }
    }

    private fun showRecordMenu(recordId: Int, isImportant: Boolean) {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_record_menu)
        val unpin = bottomSheetDialog.findViewById<LinearLayout>(R.id.view_my_categories)
        val edit = bottomSheetDialog.findViewById<LinearLayout>(R.id.view_my_templates)
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
        if (isImportant) {
            bottomSheetDialog.findViewById<TextView>(R.id.label_important)?.text =
                "Удалить из избранного"
            bottomSheetDialog.findViewById<ImageView>(R.id.important_image)?.background =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_no_star, null)
        }
        unpin?.setOnClickListener {
            mViewModel.onSetImportant(recordId, isImportant)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}