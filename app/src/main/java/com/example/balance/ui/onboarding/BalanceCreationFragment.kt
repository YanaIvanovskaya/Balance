package com.example.balance.ui.onboarding

import android.os.Bundle
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
import com.example.balance.presentation.onboarding.BalanceCreationState
import com.example.balance.presentation.onboarding.BalanceCreationViewModel
import com.example.balance.presentation.getViewModel

class BalanceCreationFragment : Fragment(R.layout.fragment_balance_creation) {

    private var mBinding: FragmentBalanceCreationBinding? = null
    private lateinit var mNavController: NavController
    private val mViewModel by getViewModel {
        BalanceCreationViewModel(
            balanceRepository = BalanceApp.balanceRepository,
            dataStore = BalanceApp.dataStore
        )
    }
    private var mSumCards: EditText? = null
    private var mSumCash: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBalanceCreationBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = findNavController()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        initWidgets()
    }

    private fun render(state: BalanceCreationState) {
        mSumCash?.setText(state.sumCash)
        mSumCards?.setText(state.sumCards)
        mBinding?.buttonStartUse?.isEnabled = state.canComplete
    }

    private fun onNextClick() {
        mViewModel.onSaveBalance()
        mNavController.navigate(R.id.bottomNavigationFragment)
    }

    private fun initWidgets() {
        mSumCards = mBinding?.sumCards
        mSumCash = mBinding?.sumCash
        mSumCards?.doAfterTextChanged {
            mViewModel.onChangeSum(mSumCash?.text.toString(), it.toString())
            mSumCards?.setSelection(it.toString().length)
        }
        mSumCash?.doAfterTextChanged {
            mViewModel.onChangeSum(it.toString(), mSumCards?.text.toString())
            mSumCash?.setSelection(it.toString().length)
        }
        mBinding?.buttonStartUse?.setOnClickListener { onNextClick() }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}
