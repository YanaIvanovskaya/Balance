package com.example.balance.ui.onboarding

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentPasscodeBinding
import com.example.balance.presentation.*

class PasscodeEntryFragment : Fragment(R.layout.fragment_passcode) {

    private var mBinding: FragmentPasscodeBinding? = null
    private val mViewModel by getViewModel {
        PasscodeEntryViewModel(
            dataStore = BalanceApp.dataStore,
            screenType = args.screenType
        )
    }
    private lateinit var navController: NavController
    private var mTextChangeListener: TextWatcher? = null
    private val args: PasscodeEntryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPasscodeBinding.inflate(inflater, container, false)
        mBinding = binding
        navController = NavHostFragment.findNavController(this)
        mViewModel.state.observe(viewLifecycleOwner, ::render)

        initButtons()

        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun render(state: PasscodeEntryState) {
        mBinding?.buttonNext?.isVisible = state.canComplete
        mBinding?.editTextCreationPasscode?.removeTextChangedListener(mTextChangeListener)
        mBinding?.editTextCreationPasscode?.setText(state.passcode)
        mTextChangeListener = mBinding?.editTextCreationPasscode?.doAfterTextChanged {
            mViewModel.onChangePasscode(it.toString())
        }
        mBinding?.editTextCreationPasscode?.inputType = state.passcodeMode

        when (state.screenType) {
            PasscodeScreenType.AUTH -> {
                mBinding?.buttonNext?.visibility = View.GONE
                mBinding?.buttonPrevious?.visibility = View.GONE
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

    private fun onNextClick() {
        mViewModel.onSavePasscode()
        navController.navigate(R.id.balanceCreationFragment)
    }

    private fun initButtons() {
        mBinding?.buttonShowPasscode?.setOnClickListener { mViewModel.onShowPasscode() }
        mBinding?.buttonClearPasscode?.setOnClickListener { mViewModel.onClickClear() }
        mBinding?.buttonPrevious?.setOnClickListener {
            navController.navigate(R.id.greetingNewUserFragment)
        }
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
