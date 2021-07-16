package com.example.balance.ui.onboarding

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.R
import com.example.balance.databinding.FragmentBalanceCreationBinding
import com.example.balance.presentation.BalanceCreationState
import com.example.balance.presentation.BalanceCreationViewModel
import com.example.balance.presentation.HomeViewModel
import com.example.balance.presentation.PasscodeEntryState

class BalanceCreationFragment : Fragment(R.layout.fragment_balance_creation) {

    private var mBinding: FragmentBalanceCreationBinding? = null
    private lateinit var navController: NavController
    private lateinit var mViewModel: BalanceCreationViewModel

    private var mSumCashChangeListener: TextWatcher? = null
    private var mSumCardsChangeListener: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBalanceCreationBinding.inflate(inflater, container, false)
        mBinding = binding
        navController = findNavController()
        mViewModel = ViewModelProvider(this).get(BalanceCreationViewModel::class.java)
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        initWidgets()
        return binding.root
    }

    private fun render(state: BalanceCreationState) {
        mBinding?.buttonStartUse?.isEnabled = state.canComplete

        mBinding?.sumCash?.removeTextChangedListener(mSumCashChangeListener)
        mBinding?.sumCash?.setText(state.sumCash)
        mSumCashChangeListener = mBinding?.sumCash?.doAfterTextChanged {
            mViewModel.onChangeSum(it.toString(), mBinding?.sumCards?.text.toString())
        }

        mBinding?.sumCards?.removeTextChangedListener(mSumCardsChangeListener)
        mBinding?.sumCards?.setText(state.sumCards)
        mSumCardsChangeListener = mBinding?.sumCards?.doAfterTextChanged {
            mViewModel.onChangeSum(mBinding?.sumCash?.text.toString(), it.toString())
        }
    }

    private fun onNextClick() {
        navController.navigate(R.id.bottomNavigationFragment)
    }

    private fun initWidgets() {
        mBinding?.buttonStartUse?.setOnClickListener { onNextClick() }
    }


    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}
