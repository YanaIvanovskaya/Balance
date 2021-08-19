package com.example.balance.ui.home

import android.content.pm.ActivityInfo
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
import com.example.balance.databinding.FragmentHomeBinding
import com.example.balance.presentation.home.HomeState
import com.example.balance.presentation.home.HomeViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.ui.menu.BottomNavigationFragmentDirections
import com.example.balance.ui.recycler_view.DividerItemDecoration
import com.example.balance.ui.recycler_view.adapter.HomeAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var mBinding: FragmentHomeBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var mHomeRecyclerView: RecyclerView
    private lateinit var mHomeAdapter: HomeAdapter
    private val mViewModel by getViewModel {
        HomeViewModel(
            balanceRepository = BalanceApp.balanceRepository,
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
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = findNavController(requireActivity(), R.id.nav_host_fragment)
        mHomeRecyclerView = binding.homeRecyclerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        mHomeAdapter = HomeAdapter(
            onLongItemClickListener = { recordId, isImportant ->
                showRecordMenu(recordId, isImportant)
                true
            },
            onClickAddListener = { onAddRecordClick() }
        )
        mViewModel.allHomeRecords.observe(viewLifecycleOwner) { list ->
            mHomeAdapter.dataSet = list.toMutableList()
            mHomeAdapter.notifyDataSetChanged()
            mViewModel.setContentLoaded(list.isNotEmpty())
        }
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        initRecyclerView()
        mBinding?.floatingButtonCreateNewRecord?.setOnClickListener { onAddRecordClick() }
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

    private fun render(state: HomeState) {
        mHomeAdapter.updateBalance(state.cash.toString(), state.cards.toString())
        mBinding?.preloaderHome?.isVisible = !(state.isContentLoaded && state.isSumLoaded)
        mBinding?.floatingButtonCreateNewRecord?.isVisible = mHomeAdapter.dataSet.size > 2
    }

    private fun initRecyclerView() {
        mHomeRecyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        ResourcesCompat.getDrawable(resources, R.drawable.item_divider, null)
            ?.let { DividerItemDecoration(it) }
            ?.let { mHomeRecyclerView.addItemDecoration(it) }
        mHomeRecyclerView.adapter = mHomeAdapter
    }

    private fun onAddRecordClick() {
        mNavController.navigate(R.id.recordCreationFragment)
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}