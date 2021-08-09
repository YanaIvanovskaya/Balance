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
import com.example.balance.R
import com.example.balance.databinding.FragmentGeneralStatisticsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart

class GeneralStatisticsFragment : Fragment(R.layout.fragment_general_statistics) {

    private var mBinding: FragmentGeneralStatisticsBinding? = null
    private lateinit var mPagerGeneralStat: ViewPager2
    private lateinit var mGeneralChartFragment: GeneralChartFragment
    private lateinit var mProfitLossChartFragment: ProfitLossChartFragment
    private lateinit var mCostsPieChartFragment: CostsPieChartFragment
    private lateinit var mProfitPieChartFragment: ProfitPieChartFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGeneralStatisticsBinding.inflate(inflater, container, false)
        mBinding = binding
        mPagerGeneralStat = binding.viewPagerGeneralStat
        mGeneralChartFragment = GeneralChartFragment()
        mProfitLossChartFragment = ProfitLossChartFragment()
        mCostsPieChartFragment = CostsPieChartFragment()
        mProfitPieChartFragment = ProfitPieChartFragment()
        mPagerGeneralStat.adapter =
            ViewPagerFragmentStateAdapter(
                requireActivity(),
                listOf(
                    mGeneralChartFragment,
                    mProfitLossChartFragment,
                    mCostsPieChartFragment,
                    mProfitPieChartFragment
                )
            )

        mPagerGeneralStat.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                (mBinding?.radioGroup?.get(position) as RadioButton).isChecked = true
            }
        })
        return binding.root
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