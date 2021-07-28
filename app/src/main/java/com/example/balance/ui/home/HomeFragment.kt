package com.example.balance.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
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
import com.example.balance.ui.recycler_view.Item
import com.example.balance.ui.recycler_view.ItemDiffUtilCallback
import com.google.android.material.bottomsheet.BottomSheetDialog


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var mBinding: FragmentHomeBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var homeAdapter: HomeAdapter
    private val mViewModel by getViewModel {
        HomeViewModel(
            recordRepository = BalanceApp.recordRepository,
            templateRepository = BalanceApp.templateRepository,
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

        homeAdapter = HomeAdapter(
            onLongItemClickListener = { recordId, position ->
                showBottomSheetDialog(recordId, position)
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
    }

    private fun updateAdapter(productList: MutableList<Item>) {
        val itemDiffUtilCallback = ItemDiffUtilCallback(homeAdapter.dataSet, productList)
        val itemDiffResult = DiffUtil.calculateDiff(itemDiffUtilCallback)
        homeAdapter.dataSet = productList
        itemDiffResult.dispatchUpdatesTo(homeAdapter)
    }

    private fun showBottomSheetDialog(recordId: Int, position: Int) {
        val bottomSheetDialog  = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.fragment_bottom_sheet)
        val unpin = bottomSheetDialog.findViewById<LinearLayout>(R.id.pin_unpin_record)
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
        unpin?.setOnClickListener {
            mViewModel.onUnpinClick(recordId)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun render(state: HomeState) {
        homeAdapter.updateBalance(mViewModel.getCurrentCash(), mViewModel.getCurrentCards())
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