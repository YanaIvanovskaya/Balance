package com.example.balance.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentPasscodeBinding
import com.example.balance.presentation.PasscodeScreenType
import com.example.balance.presentation.auth.PasscodeEntryState
import com.example.balance.presentation.auth.PasscodeEntryViewModel
import com.example.balance.presentation.getViewModel

class PasscodeEntryFragment : Fragment(R.layout.fragment_passcode) {

    private var mBinding: FragmentPasscodeBinding? = null
    private lateinit var mNavController: NavController
    private val mViewModel by getViewModel {
        PasscodeEntryViewModel(
            dataStore = BalanceApp.dataStore,
            recordRepository = BalanceApp.recordRepository,
            templateRepository = BalanceApp.templateRepository,
            screenType = args.screenType
        )
    }
    private val args: PasscodeEntryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPasscodeBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = NavHostFragment.findNavController(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        applyScreenType()
        initButtons()
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
                    requireContext().resources.getString(R.string.enter_the_passcode)
                mBinding?.viewForgotThePasscode?.visibility = View.VISIBLE
            }
            PasscodeScreenType.ONBOARDING -> {
                mBinding?.viewForgotThePasscode?.visibility = View.INVISIBLE
            }
            PasscodeScreenType.SETTINGS -> {
                mBinding?.titlePasscodeEntry?.text = "Придумайте новый пароль"
                mBinding?.buttonNext?.visibility = View.INVISIBLE
                mBinding?.viewForgotThePasscode?.visibility = View.INVISIBLE
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
            PasscodeScreenType.ONBOARDING, PasscodeScreenType.SETTINGS -> {
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
        }
    }

    private fun onNextClick() {
        mViewModel.onSavePasscode()
        when (args.screenType) {
            PasscodeScreenType.ONBOARDING -> mNavController.navigate(R.id.balanceCreationFragment)
            PasscodeScreenType.SETTINGS -> mNavController.navigate(R.id.bottomNavigationFragment)
            else -> Unit
        }
    }

    private fun onForgotPasscodeClick() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.dialog_access_recovery)
            .setPositiveButton("Продолжить") { _, _ ->
                mViewModel.onAccessRecovery()
                mNavController.navigate(R.id.passcodeUpdatingFragment)
            }.setNegativeButton("Отмена", null)
        val dialog = builder.create()
        dialog.show()
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
        buttonKeyBoard.forEach {
            it?.setOnClickListener { _ ->
                mViewModel.onNumberClick(it.text as String)
            }
        }
        mBinding?.viewForgotThePasscode?.setOnClickListener { onForgotPasscodeClick() }
    }

}
