package com.example.balance.ui.onboarding

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.balance.BuiltInKeyBoard
import com.example.balance.R
import com.example.balance.data.UserDataStore
import com.example.balance.databinding.FragmentPasscodeCreationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PasscodeCreationFragment : Fragment(R.layout.fragment_passcode_creation), BuiltInKeyBoard {

    private var mBinding: FragmentPasscodeCreationBinding? = null
    private lateinit var mViewModel: PasscodeCreationViewModel
    private lateinit var navController: NavController

    private lateinit var userDataStore: UserDataStore

    override var passcodeField: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPasscodeCreationBinding.inflate(inflater, container, false)
        mBinding = binding
        mViewModel = ViewModelProvider(this).get(PasscodeCreationViewModel::class.java)
        navController = NavHostFragment.findNavController(this)
        passcodeField = mBinding?.editTextCreationPasscode

        initButtons()
        mBinding?.editTextCreationPasscode?.setText(mViewModel.passcode)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userDataStore = UserDataStore(view.context)
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun onChangePasscode() {
        val passcode = mBinding?.editTextCreationPasscode?.text.toString().trim()
        mViewModel.passcode = passcode
//        println("onChangePasscode ${mViewModel.passcode}")
        val nextBtn: Button? = mBinding?.buttonNext
        if (passcode.length == 5)
            nextBtn?.visibility = View.VISIBLE
        else
            nextBtn?.visibility = View.INVISIBLE
    }

    private fun onNextClick() {
//        val passcode = mBinding?.editTextCreationPasscode?.text.toString().trim()
        mViewModel.savePasscode(userDataStore)
        navController.navigate(R.id.creatingBalanceFragment)
//        println("onNextClick ${mViewModel.passcode}")
    }

    private fun initButtons() {
        mBinding?.buttonPrevious?.setOnClickListener {
            navController.navigate(R.id.greetingNewUserFragment)
        }
        mBinding?.buttonNext?.setOnClickListener { onNextClick() }
        mBinding?.buttonClearPasscode?.setOnClickListener { onClickClear() }
        mBinding?.editTextCreationPasscode?.addTextChangedListener { onChangePasscode() }
        val b = mBinding
        connectKeyBoardClickListeners(
            listOf(
                b?.button0,
                b?.button1,
                b?.button2,
                b?.button3,
                b?.button4,
                b?.button5,
                b?.button6,
                b?.button7,
                b?.button8,
                b?.button9
            )
        )
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


class PasscodeCreationViewModel : ViewModel() {
    var passcode: String = ""

    fun savePasscode(dataStore: UserDataStore) {
        viewModelScope.launch(Dispatchers.Main) {
            dataStore.savePasscode(passcode)
            println("PASSCODE SAVED SUCCESSFULLY -> ${dataStore.passcode}")
        }
    }

}
