package com.example.balance.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentStatisticsBinding
import com.example.balance.presentation.statistics.StatisticsState
import com.example.balance.presentation.statistics.StatisticsViewModel
import com.example.balance.presentation.getViewModel

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private var mBinding: FragmentStatisticsBinding? = null
    private lateinit var mNavController: NavController
    private lateinit var mGeneralStatisticsFragment: GeneralStatisticsFragment
    private lateinit var mProfitsStatisticsFragment: ProfitsStatisticsFragment
    private lateinit var mCostsStatisticsFragment: CostsStatisticsFragment
    private val mViewModel by getViewModel {
        StatisticsViewModel(
            recordRepository = BalanceApp.recordRepository,
            categoryRepository = BalanceApp.categoryRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = findNavController()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mGeneralStatisticsFragment = GeneralStatisticsFragment()
        mProfitsStatisticsFragment = ProfitsStatisticsFragment()
        mCostsStatisticsFragment = CostsStatisticsFragment()
        mViewModel.init()
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        mBinding?.frame?.let {
            childFragmentManager.beginTransaction()
                .add(it.id, mGeneralStatisticsFragment)
                .show(mGeneralStatisticsFragment)
                .commit()
        }
        initChips()
    }

    private fun initChips() {
        mBinding?.chipGeneral?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding?.frame?.let {
                    childFragmentManager.beginTransaction()
                        .replace(it.id, mGeneralStatisticsFragment)
                        .show(mGeneralStatisticsFragment)
                        .commit()
                }
            }
        }
        mBinding?.chipCosts?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding?.frame?.let {
                    childFragmentManager.beginTransaction()
                        .replace(it.id, mCostsStatisticsFragment)
                        .show(mCostsStatisticsFragment)
                        .commit()
                }
                mCostsStatisticsFragment.setData(mViewModel.getCostsStatItems())
            }
        }
        mBinding?.chipProfit?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding?.frame?.let {
                    childFragmentManager.beginTransaction()
                        .replace(it.id, mProfitsStatisticsFragment)
                        .show(mProfitsStatisticsFragment)
                        .commit()
                }
                mProfitsStatisticsFragment.setData(mViewModel.getProfitStatItems())
            }
        }
    }

    private fun render(state: StatisticsState) {
        if (state.isContentLoaded) {
            mBinding?.frameNoStatistics?.isVisible = state.hasNoRecords
            mBinding?.chipGroup?.isVisible = !state.hasNoRecords
            mBinding?.frame?.isVisible = !state.hasNoRecords
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}



