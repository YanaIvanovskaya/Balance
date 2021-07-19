package com.example.balance.ui.home

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.databinding.FragmentRecordCreationBinding
import com.example.balance.presentation.*

class RecordCreationFragment : Fragment(R.layout.fragment_record_creation) {

    private var mBinding: FragmentRecordCreationBinding? = null
    private val mViewModel: RecordCreationViewModel by getViewModel {
        RecordCreationViewModel(
            repository = BalanceApp.repository
        )
    }
    private lateinit var mNavController: NavController
    private var mSumChangeListener: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecordCreationBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = findNavController()
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
        mBinding?.buttonCreateAndSaveNewRecord?.isEnabled = state.canSave
        mBinding?.errorMsgSumOfMoney?.isVisible = !state.canSave

        mBinding?.editTextSumOfOperation?.removeTextChangedListener(mSumChangeListener)
        mBinding?.editTextSumOfOperation?.setText(state.sumRecord)
        mSumChangeListener = mBinding?.editTextSumOfOperation?.doAfterTextChanged {
            mViewModel.onChangeSum(it.toString())
        }
    }

    private fun onCreateRecord() {
        mViewModel.onSaveRecord()
        mNavController.popBackStack()
    }


    private fun initButtons() {
//        mBinding?.radioButtonCash?.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
//            if (compoundButton.isShown && compoundButton.isChecked) {
//
//            }
//        }
//        mBinding?.radioButtonCosts?.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
//            if (compoundButton.isShown && compoundButton.isChecked) {
//
//            }
//        }

        mBinding?.toolbarNewRecord?.setNavigationOnClickListener {
            mNavController.popBackStack()
        }
        mBinding?.buttonCreateAndSaveNewRecord?.setOnClickListener { onCreateRecord() }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mNavController.popBackStack()
                }
            }
        )
    }
}