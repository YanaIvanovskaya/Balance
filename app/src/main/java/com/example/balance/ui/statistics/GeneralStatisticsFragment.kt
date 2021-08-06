package com.example.balance.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var mRestBarChart: BarChart
    private lateinit var chart: PieChart
    private lateinit var mPagerGeneralStat: ViewPager2

    private lateinit var mGeneralChartFragment: GeneralChartFragment
    private lateinit var mProfitLossChartFragment: ProfitLossChartFragment
    private lateinit var mCostsPieChartFragment: CostsPieChartFragment
    private lateinit var mProfitPieChartFragment: ProfitPieChartFragment

//    private val onRestChartValueSelectedListener = object : OnChartValueSelectedListener {
//        override fun onValueSelected(e: Entry?, h: Highlight?) {
//            if (e is BarEntry) {
//                val month = getMonthName(e.data as Int, Case.IN)
//                val sumMoney = e.y.toInt()
//                val message =
//                    "В $month ${
//                        if (sumMoney > 0) "получена прибыль"
//                        else "получен убыток"
//                    } - ${abs(sumMoney)} P"
//                mBinding?.descriptionChartRest?.text = message
//            }
//        }
//
//        override fun onNothingSelected() {}
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGeneralStatisticsBinding.inflate(inflater, container, false)
        mBinding = binding
        mRestBarChart = binding.restChart
        chart = binding.pieChart

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