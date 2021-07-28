package com.example.balance.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentPasscodeBinding
import com.example.balance.presentation.PasscodeEntryState
import com.example.balance.presentation.PasscodeEntryViewModel
import com.example.balance.presentation.PasscodeScreenType
import com.example.balance.presentation.getViewModel

class PasscodeEntryFragment : Fragment(R.layout.fragment_passcode) {

    private var mBinding: FragmentPasscodeBinding? = null
    private val mViewModel by getViewModel {
        PasscodeEntryViewModel(
            dataStore = BalanceApp.dataStore,
            screenType = args.screenType
        )
    }
    private lateinit var mNavController: NavController
    private val args: PasscodeEntryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPasscodeBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = NavHostFragment.findNavController(this)
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        applyScreenType()
        initButtons()
        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun applyScreenType() {
        when (args.screenType) {
            PasscodeScreenType.AUTH -> {
                mBinding?.buttonNext?.visibility = View.INVISIBLE
                mBinding?.buttonShowPasscode?.visibility = View.GONE
                mBinding?.titlePasscodeEntry?.text =
                    context?.resources?.getString(R.string.greeting)
            }
            PasscodeScreenType.ONBOARDING -> {
            }
            PasscodeScreenType.SETTINGS -> {
            }
        }
    }

    private fun render(state: PasscodeEntryState) {
        mBinding?.editTextCreationPasscode?.setText(state.passcode)

        when (args.screenType) {
            PasscodeScreenType.ONBOARDING -> {
                mBinding?.buttonNext?.isVisible = state.canComplete
                mBinding?.editTextCreationPasscode?.inputType = state.passcodeMode
            }
            PasscodeScreenType.AUTH -> {
                mBinding?.errorMsgPasscode?.isVisible = state.canComplete && !state.isMatches
                if (state.canComplete and state.isMatches) {
                    mNavController.navigate(R.id.bottomNavigationFragment)
                }
            }
            else -> Unit
        }
    }

    private fun onNextClick() {
        mViewModel.onSavePasscode()
        mNavController.navigate(R.id.balanceCreationFragment)
    }

    private fun initButtons() {
        mBinding?.buttonShowPasscode?.setOnClickListener { mViewModel.onShowPasscode() }
        mBinding?.buttonClearPasscode?.setOnClickListener { mViewModel.onClickClear() }
        mBinding?.buttonNext?.setOnClickListener { onNextClick() }

        val buttonKeyBoard = listOf(
            mBinding?.button0,
            mBinding?.button1,
            mBinding?.button2,
            mBinding?.button3,
            mBinding?.button4,
            mBinding?.button5,
            mBinding?.button6,
            mBinding?.button7,
            mBinding?.button8,
            mBinding?.button9
        )
        for (button in buttonKeyBoard)
            button?.setOnClickListener { mViewModel.onNumberClick(button.text as String) }
    }

}
