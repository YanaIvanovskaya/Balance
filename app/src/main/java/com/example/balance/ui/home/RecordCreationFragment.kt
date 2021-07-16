package com.example.balance.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.R
import com.example.balance.databinding.FragmentRecordCreationBinding
import com.example.balance.presentation.PasscodeEntryState
import com.example.balance.presentation.RecordCreationState
import com.example.balance.presentation.RecordCreationViewModel

class RecordCreationFragment : Fragment(R.layout.fragment_record_creation) {

    private var mBinding: FragmentRecordCreationBinding? = null
    private lateinit var mViewModel: RecordCreationViewModel
    private lateinit var navController: NavController
    private var mSumChangeListener: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecordCreationBinding.inflate(inflater, container, false)
        mBinding = binding
        mViewModel = ViewModelProvider(this).get(RecordCreationViewModel::class.java)
        navController = findNavController()
        mViewModel.state.observe(viewLifecycleOwner, ::render)

        initButtons()
        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun render(state: RecordCreationState) {
        mBinding?.radioButtonCosts?.isChecked = state.isCosts
        mBinding?.radioButtonCash?.isChecked = state.isCash
        mBinding?.buttonCreateAndSaveNewRecord?.isVisible = state.canSave
        mBinding?.errorMsgSumOfMoney?.isVisible = !state.canSave

        mBinding?.editTextSumOfOperation?.removeTextChangedListener(mSumChangeListener)
        mBinding?.editTextSumOfOperation?.setText(state.sumRecord)
        mSumChangeListener = mBinding?.editTextSumOfOperation?.doAfterTextChanged {
            mViewModel.onChangeSum(it.toString())
        }
    }

    private fun onCreateRecord() {
        navController.popBackStack()
    }

    private fun initButtons() {
        mBinding?.toolbarNewRecord?.setNavigationOnClickListener {
            navController.popBackStack()
        }
        mBinding?.buttonCreateAndSaveNewRecord?.setOnClickListener { onCreateRecord() }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController.popBackStack()
                }
            }
        )
    }
}