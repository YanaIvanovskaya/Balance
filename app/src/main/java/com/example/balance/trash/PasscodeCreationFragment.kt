//package com.example.balance.ui.onboarding
//
//import android.os.Bundle
//import android.text.Editable
//import android.text.InputType
//import android.text.TextWatcher
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import androidx.core.view.isVisible
//import androidx.core.widget.doAfterTextChanged
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.NavController
//import androidx.navigation.fragment.NavHostFragment
//import com.example.balance.trash.BuiltInKeyBoard
//import com.example.balance.R
//import com.example.balance.data.UserDataStore
//import com.example.balance.databinding.FragmentPasscodeCreationBinding
//import com.example.balance.presentation.PasscodeCreationState
//import com.example.balance.presentation.PasscodeCreationViewModel
//import com.example.balance.presentation.ViewModelFactory
//
//class PasscodeCreationFragment : Fragment(R.layout.fragment_passcode_creation), BuiltInKeyBoard {
//
//    private var mBinding: FragmentPasscodeCreationBinding? = null
//    private val mViewModel by viewModels<PasscodeCreationViewModel> {
//        ViewModelFactory()
//    }
//    private lateinit var navController: NavController
//
//    private lateinit var userDataStore: UserDataStore
//
//    override var passcodeField: EditText? = null
//
//    private var mTextChangeListener: TextWatcher? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentPasscodeCreationBinding.inflate(inflater, container, false)
//        mBinding = binding
//
//        navController = NavHostFragment.findNavController(this)
//        passcodeField = mBinding?.editTextCreationPasscode
//
//        mViewModel.state.observe(viewLifecycleOwner, ::render)
//
//        initButtons()
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        userDataStore = UserDataStore(view.context)
//    }
//
//    override fun onDestroyView() {
//        mBinding = null
//        super.onDestroyView()
//    }
//
//    private fun render(state: PasscodeCreationState) {
//        mBinding?.buttonNext?.isVisible = state.canComplete
//
//        mBinding?.editTextCreationPasscode?.removeTextChangedListener(mTextChangeListener)
//        mBinding?.editTextCreationPasscode?.setText(state.passcode)
//        mTextChangeListener = mBinding?.editTextCreationPasscode?.doAfterTextChanged {
//            mViewModel.onChangePasscode(it.toString())
//        }
//    }
//
//    private fun onNextClick() {
//        mViewModel.onSavePasscode()
//        navController.navigate(R.id.creatingBalanceFragment)
//    }
//
//    private fun initButtons() {
//        mBinding?.buttonPrevious?.setOnClickListener {
//            navController.navigate(R.id.greetingNewUserFragment)
//        }
//        mBinding?.buttonNext?.setOnClickListener { onNextClick() }
//
//        mBinding?.buttonClearPasscode?.setOnClickListener { mViewModel.onClickClear() }
//
//        mBinding?.button0?.setOnClickListener { mViewModel.onNumberClick(0) }
//        mBinding?.button1?.setOnClickListener { mViewModel.onNumberClick(1) }
//        mBinding?.button2?.setOnClickListener { mViewModel.onNumberClick(2) }
//        mBinding?.button3?.setOnClickListener { mViewModel.onNumberClick(3) }
//        mBinding?.button4?.setOnClickListener { mViewModel.onNumberClick(4) }
//        mBinding?.button5?.setOnClickListener { mViewModel.onNumberClick(5) }
//        mBinding?.button6?.setOnClickListener { mViewModel.onNumberClick(6) }
//        mBinding?.button7?.setOnClickListener { mViewModel.onNumberClick(7) }
//        mBinding?.button8?.setOnClickListener { mViewModel.onNumberClick(8) }
//        mBinding?.button9?.setOnClickListener { mViewModel.onNumberClick(9) }
//
//        mBinding?.buttonShowPasscode?.setOnClickListener { onShowPasscode() }
//    }
//
//    private fun onShowPasscode() {
//        val passcodeField = mBinding?.editTextCreationPasscode
//        val inputType = passcodeField?.inputType
//        val passcodeType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
//        val normalType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
//
//        if (inputType == passcodeType)
//            passcodeField.inputType = normalType
//        else
//            passcodeField?.inputType = passcodeType
//    }
//
//}
