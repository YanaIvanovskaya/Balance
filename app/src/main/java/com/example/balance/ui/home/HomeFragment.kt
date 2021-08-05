package com.example.balance.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentHomeBinding
import com.example.balance.presentation.HomeState
import com.example.balance.presentation.HomeViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.ui.menu.BottomNavigationFragmentDirections
import com.example.balance.ui.recycler_view.HomeAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import timber.log.Timber


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var mBinding: FragmentHomeBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var homeAdapter: HomeAdapter
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
        homeRecyclerView = binding.homeRecyclerView
        mNavController = findNavController(requireActivity(), R.id.nav_host_fragment)

        homeAdapter = HomeAdapter(
            onLongItemClickListener = { recordId ->
                showRecordMenu(recordId)
                true
            }
        )

        mViewModel.state.observe(viewLifecycleOwner, ::render)
        initRecyclerView()
        binding.floatingButtonCreateNewRecord.setOnClickListener { onAddRecordClick() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.allHomeRecords.observe(viewLifecycleOwner) { list ->
            homeAdapter.dataSet = list.toMutableList()
            homeAdapter.notifyDataSetChanged()
        }
        println("onViewCreated")
    }

    override fun onResume() {
        super.onResume()
        println("Resume")
        mBinding?.splash?.visibility = View.GONE
        mBinding?.floatingButtonCreateNewRecord?.isVisible = true
    }

    private fun showRecordMenu(recordId: Int) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
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
        unpin?.setOnClickListener {
            mViewModel.onUnpinClick(recordId)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun render(state: HomeState) {
        Timber.d("Render state")
        Timber.d(state.toString())
        homeAdapter.updateBalance(state.cash.toString(), state.cards.toString())
    }

    private fun initRecyclerView() {
        homeRecyclerView.adapter = homeAdapter
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