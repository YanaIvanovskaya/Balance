package com.example.balance.ui.onboarding

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentBalanceCreationBinding
import com.example.balance.presentation.BalanceCreationState
import com.example.balance.presentation.BalanceCreationViewModel
import com.example.balance.presentation.getViewModel

class BalanceCreationFragment : Fragment(R.layout.fragment_balance_creation) {

    private var mBinding: FragmentBalanceCreationBinding? = null
    private lateinit var navController: NavController
    private val mViewModel by getViewModel {
        BalanceCreationViewModel(
            dataStore = BalanceApp.dataStore
        )
    }
    private var mSumCashChangeListener: TextWatcher? = null
    private var mSumCardsChangeListener: TextWatcher? = null

    private var mSumCards: EditText? = null
    private var mSumCash: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBalanceCreationBinding.inflate(inflater, container, false)
        mBinding = binding
        navController = findNavController()
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        initWidgets()
        return binding.root
    }

    private fun render(state: BalanceCreationState) {
        mBinding?.buttonStartUse?.isEnabled = state.canComplete

        mSumCash?.removeTextChangedListener(mSumCashChangeListener)
        mSumCash?.setText(state.sumCash)
        mSumCashChangeListener = mSumCash?.doAfterTextChanged {
            mViewModel.onChangeSum(it.toString(), mSumCards?.text.toString())
            mSumCash?.setSelection(it.toString().length)
        }

        mSumCards?.removeTextChangedListener(mSumCardsChangeListener)
        mSumCards?.setText(state.sumCards)
        mSumCardsChangeListener = mSumCards?.doAfterTextChanged {
            mViewModel.onChangeSum(mSumCash?.text.toString(), it.toString())
            mSumCards?.setSelection(it.toString().length)
        }
    }

    private fun onNextClick() {
        mViewModel.onSaveBalance()
        navController.navigate(R.id.bottomNavigationFragment)
    }

    private fun initWidgets() {
        mSumCards = mBinding?.sumCards
        mSumCash = mBinding?.sumCash
        mBinding?.buttonStartUse?.setOnClickListener { onNextClick() }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}
