//package com.example.balance.ui.auth
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import androidx.core.widget.addTextChangedListener
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.navigation.NavController
//import androidx.navigation.fragment.NavHostFragment
//import com.example.balance.trash.BuiltInKeyBoard
//import com.example.balance.R
//import com.example.balance.databinding.FragmentPasscodeEntryBinding
//import java.util.*
//
//class PasscodeEntryFragment : Fragment(R.layout.fragment_passcode_entry), BuiltInKeyBoard {
//    private var mBinding: FragmentPasscodeEntryBinding? = null
//    private lateinit var mViewModel: PasscodeEntryViewModel
//    private lateinit var navController: NavController
//
//    override var passcodeField: EditText? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentPasscodeEntryBinding.inflate(inflater, container, false)
//        mBinding = binding
//        mViewModel = ViewModelProvider(this).get(PasscodeEntryViewModel::class.java)
//        navController = NavHostFragment.findNavController(this)
//        passcodeField = mBinding?.editTextEntryPasscode
//
//        initButtons()
//        mBinding?.editTextEntryPasscode?.setText(mViewModel.passcode)
//        mBinding?.titleGreetingUser?.text = mViewModel.getGreetingMessage()
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        mBinding = null
//        super.onDestroyView()
//    }
//
//    private fun onChangePasscode() {
//        val passcode = mBinding?.editTextEntryPasscode?.text.toString().trim()
//        mViewModel.passcode = passcode
//        println("onChangePasscode ${mViewModel.passcode}")
//    }
//
//    private fun initButtons() {
//        mBinding?.buttonClearPasscode?.setOnClickListener { onClickClear() }
//        mBinding?.editTextEntryPasscode?.addTextChangedListener { onChangePasscode() }
//        val b = mBinding
//        connectKeyBoardClickListeners(
//            listOf(
//                b?.button0,
//                b?.button1,
//                b?.button2,
//                b?.button3,
//                b?.button4,
//                b?.button5,
//                b?.button6,
//                b?.button7,
//                b?.button8,
//                b?.button9
//            )
//        )
//    }
//
//}
//
//class PasscodeEntryViewModel : ViewModel() {
//    var passcode: String = ""
//
//    fun getGreetingMessage() = when (Calendar.HOUR) {
//        in 6..11 -> "Доброе утро!"
//        in 12..18 -> "Добрый день!"
//        in 19..22 -> "Добрый вечер!"
//        else -> "Доброй ночи!"
//    }
//
//}