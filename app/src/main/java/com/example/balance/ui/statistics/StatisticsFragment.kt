package com.example.balance.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentStatisticsBinding
import com.example.balance.presentation.StatisticsViewModel
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
        mGeneralStatisticsFragment = GeneralStatisticsFragment()
        mProfitsStatisticsFragment = ProfitsStatisticsFragment()
        mCostsStatisticsFragment = CostsStatisticsFragment()

        binding.chipGeneral.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                childFragmentManager.beginTransaction()
                    .replace(binding.frame.id, mGeneralStatisticsFragment)
                    .show(mGeneralStatisticsFragment)
                    .commit()

            }
        }

        binding.chipCosts.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                childFragmentManager.beginTransaction()
                    .replace(binding.frame.id,mCostsStatisticsFragment)
                    .show(mCostsStatisticsFragment)
                    .commit()
                mCostsStatisticsFragment.setData(mViewModel.getCostsStatItems())
            }
        }

        binding.chipProfit.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                childFragmentManager.beginTransaction()
                    .replace(binding.frame.id, mProfitsStatisticsFragment)
                    .show(mProfitsStatisticsFragment)
                    .commit()
                mProfitsStatisticsFragment.setData(mViewModel.getProfitStatItems())
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding?.frame?.let {
            childFragmentManager.beginTransaction()
                .add(it.id, mGeneralStatisticsFragment)
                .show(mGeneralStatisticsFragment)
                .commit()
        }

    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }



}




