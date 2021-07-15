package com.example.balance.ui.onboarding

import ViewModelFactory
import android.os.Bundle
import android.text.InputType
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
import com.example.balance.R
import com.example.balance.databinding.FragmentPasscodeBinding
import com.example.balance.presentation.PasscodeEntryState
import com.example.balance.presentation.PasscodeEntryViewModel
import com.example.balance.presentation.PasscodeScreenType

class PasscodeEntryFragment : Fragment(R.layout.fragment_passcode) {

    private var mBinding: FragmentPasscodeBinding? = null
    private val mViewModel by viewModels<PasscodeEntryViewModel> { ViewModelFactory() }
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

        mViewModel.state.value = mViewModel.state.value?.copy(
            passcode = "",
            canComplete = false,
            screenType = args.screenType
        )
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
        navController.navigate(R.id.creatingBalanceFragment)
    }

    private fun initButtons() {
        mBinding?.buttonPrevious?.setOnClickListener {
            navController.navigate(R.id.greetingNewUserFragment)
        }
        mBinding?.buttonNext?.setOnClickListener { onNextClick() }

        mBinding?.buttonClearPasscode?.setOnClickListener { mViewModel.onClickClear() }

        mBinding?.button0?.setOnClickListener { mViewModel.onNumberClick(0) }
        mBinding?.button1?.setOnClickListener { mViewModel.onNumberClick(1) }
        mBinding?.button2?.setOnClickListener { mViewModel.onNumberClick(2) }
        mBinding?.button3?.setOnClickListener { mViewModel.onNumberClick(3) }
        mBinding?.button4?.setOnClickListener { mViewModel.onNumberClick(4) }
        mBinding?.button5?.setOnClickListener { mViewModel.onNumberClick(5) }
        mBinding?.button6?.setOnClickListener { mViewModel.onNumberClick(6) }
        mBinding?.button7?.setOnClickListener { mViewModel.onNumberClick(7) }
        mBinding?.button8?.setOnClickListener { mViewModel.onNumberClick(8) }
        mBinding?.button9?.setOnClickListener { mViewModel.onNumberClick(9) }

        mBinding?.buttonShowPasscode?.setOnClickListener { onShowPasscode() }
    }

    private fun onShowPasscode() {
        val passcodeField = mBinding?.editTextCreationPasscode
        val inputType = passcodeField?.inputType
        val passcodeType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        val normalType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL

        if (inputType == passcodeType)
            passcodeField.inputType = normalType
        else
            passcodeField?.inputType = passcodeType
    }

}
