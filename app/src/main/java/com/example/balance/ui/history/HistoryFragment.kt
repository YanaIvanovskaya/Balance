package com.example.balance.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
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
    private lateinit var mHistoryRecyclerView: RecyclerView
    private lateinit var mHistoryAdapter: HistoryAdapter
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
        mHistoryRecyclerView = binding.historyRecyclerView
        mNavController = findNavController(requireActivity(), R.id.nav_host_fragment)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mHistoryAdapter = HistoryAdapter(
            onLongItemClickListener = { recordId,isImportant ->
                showRecordMenu(recordId, isImportant)
                true
            })
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        initRecyclerView()
        initChips()
    }

    private fun render(state: HistoryState) {
        mBinding?.textNoItems?.text = when (state.currentChip) {
            0 -> {
                mBinding?.chipAllHistory?.isChecked = true
                mHistoryAdapter.dataSet = state.allRecords
                "Здесь будет общая история ваших записей"
            }
            1 -> {
                mBinding?.chipProfitHistory?.isChecked = true
                mHistoryAdapter.dataSet = state.profitRecords
                "Здесь будет история ваших доходов"
            }
            2 -> {
                mBinding?.chipCostsHistory?.isChecked = true
                mHistoryAdapter.dataSet = state.costsRecords
                "Здесь будет история ваших расходов"
            }
            3 -> {
                mBinding?.chipImportantHistory?.isChecked = true
                mHistoryAdapter.dataSet = state.importantRecords
                "Здесь будет история ваших\n избранных записей"
            }
            else -> ""
        }
        mBinding?.imageNoItems?.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                when (state.currentChip) {
                    0 -> R.drawable.ic_menu_history
                    1 -> R.drawable.ic_profits
                    2 -> R.drawable.ic_loss
                    else -> R.drawable.ic_star
                }, null
            )
        )
        mHistoryAdapter.notifyDataSetChanged()
        val hasNoItems = mHistoryAdapter.dataSet.isEmpty()
        mHistoryRecyclerView.visibility = if (hasNoItems) View.INVISIBLE else View.VISIBLE
        mBinding?.frameNoItems?.isVisible = hasNoItems && state.isContentLoaded
        mBinding?.preloaderHistory?.visibility =
            if (state.hasNoRecords || state.isContentLoaded) View.GONE
            else View.VISIBLE
    }

    private fun initRecyclerView() {
        mHistoryRecyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        ResourcesCompat.getDrawable(resources, R.drawable.item_divider, null)
            ?.let { DividerItemDecoration(it) }
            ?.let { mHistoryRecyclerView.addItemDecoration(it) }
        mHistoryAdapter.setHasStableIds(true)
        mHistoryRecyclerView.adapter = mHistoryAdapter
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