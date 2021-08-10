package com.example.balance.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentGeneralStatisticsBinding
import com.example.balance.presentation.GeneralStatisticsState
import com.example.balance.presentation.GeneralStatisticsViewModel
import com.example.balance.presentation.getViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart

class GeneralStatisticsFragment : Fragment(R.layout.fragment_general_statistics) {

    private var mBinding: FragmentGeneralStatisticsBinding? = null
    private var mPagerGeneralStat: ViewPager2? = null
    private lateinit var mGeneralChartFragment: GeneralChartFragment
    private lateinit var mProfitLossChartFragment: ProfitLossChartFragment
    private lateinit var mCostsPieChartFragment: CostsPieChartFragment
    private lateinit var mProfitPieChartFragment: ProfitPieChartFragment

    private lateinit var mFragmentStateAdapter: ViewPagerFragmentStateAdapter

    private val mViewModel by getViewModel {
        GeneralStatisticsViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGeneralStatisticsBinding.inflate(inflater, container, false)
        mBinding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mGeneralChartFragment = GeneralChartFragment()
        mProfitLossChartFragment = ProfitLossChartFragment()
        mCostsPieChartFragment = CostsPieChartFragment()
        mProfitPieChartFragment = ProfitPieChartFragment()

        mPagerGeneralStat = mBinding?.viewPagerGeneralStat
        mFragmentStateAdapter = ViewPagerFragmentStateAdapter(
            requireActivity(),
            listOf(
                mGeneralChartFragment,
                mProfitLossChartFragment,
                mCostsPieChartFragment,
                mProfitPieChartFragment
            )
        )
        mPagerGeneralStat?.adapter = mFragmentStateAdapter
        mPagerGeneralStat?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mViewModel.savePagePosition(position)
                super.onPageSelected(position)
            }
        })
        mViewModel.state.observe(viewLifecycleOwner, ::render)
    }

    private fun render(state: GeneralStatisticsState) {
        mBinding?.viewPagerGeneralStat?.currentItem = state.currentPagePosition
        (mBinding?.radioGroup?.get(state.currentPagePosition) as RadioButton).isChecked = true
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private class ViewPagerFragmentStateAdapter(
        activity: FragmentActivity,
        private val fragmentList: List<Fragment>
    ) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

    }

}