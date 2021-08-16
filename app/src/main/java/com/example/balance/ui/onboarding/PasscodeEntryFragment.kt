package com.example.balance.ui.onboarding

import android.content.pm.ActivityInfo
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
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
        val passcodeLength = state.passcode.length

        mBinding?.char1?.isChecked = passcodeLength >= 1
        mBinding?.char2?.isPressed = passcodeLength == 1
        mBinding?.char2?.isChecked = passcodeLength >= 2
        mBinding?.char3?.isPressed = passcodeLength == 2
        mBinding?.char3?.isChecked = passcodeLength >= 3
        mBinding?.char4?.isPressed = passcodeLength == 3
        mBinding?.char4?.isChecked = passcodeLength >= 4
        mBinding?.char5?.isPressed = passcodeLength == 4
        mBinding?.char5?.isChecked = passcodeLength == 5

        when (args.screenType) {
            PasscodeScreenType.ONBOARDING -> {
                mBinding?.buttonNext?.visibility = when (state.canComplete) {
                    true -> View.VISIBLE
                    false -> View.INVISIBLE
                }
            }
            PasscodeScreenType.AUTH -> {
                val incorrectPasscode = state.canComplete && !state.isMatches
                mBinding?.errorMsgPasscode?.visibility = when (incorrectPasscode) {
                    true -> View.VISIBLE
                    false -> View.INVISIBLE
                }
                mBinding?.char1?.isActivated = incorrectPasscode
                mBinding?.char2?.isActivated = incorrectPasscode
                mBinding?.char3?.isActivated = incorrectPasscode
                mBinding?.char4?.isActivated = incorrectPasscode
                mBinding?.char5?.isActivated = incorrectPasscode

                mBinding?.progressBar4?.visibility =
                    if (state.canComplete and state.isMatches) {
                        mNavController.navigate(R.id.bottomNavigationFragment)
                        View.VISIBLE
                    } else View.INVISIBLE
            }
            else -> Unit
        }
    }

    private fun onNextClick() {
        mViewModel.onSavePasscode()
        mNavController.navigate(R.id.balanceCreationFragment)
    }

    private fun initButtons() {
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
