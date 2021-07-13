package com.example.balance.ui.home

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.balance.R
import com.example.balance.databinding.FragmentRecordCreationBinding
import com.example.balance.presentation.Record
import com.example.balance.presentation.RecordCreationViewModel

class RecordCreationFragment : Fragment(R.layout.fragment_record_creation) {

    private var mBinding: FragmentRecordCreationBinding? = null
    private lateinit var mViewModel: RecordCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecordCreationBinding.inflate(inflater, container, false)
        mBinding = binding
        mViewModel = ViewModelProvider(this).get(RecordCreationViewModel::class.java)

        initBackButtons()
        initWidgets()
        binding.buttonCreateAndSaveNewRecord.setOnClickListener { onCreateRecord() }
        binding.editTextSumOfOperation.addTextChangedListener { onChangeSum() }
        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun onCreateRecord() {
//        val newRecord = Record()

        findNavController().popBackStack()

    }

    private fun initBackButtons() {
        mBinding?.toolbarNewRecord?.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun initWidgets() {
//        println("WERTYUI${mViewModel.sumOfRecord.toString()}")
        mBinding?.editTextSumOfOperation?.setText(mViewModel.sumOfRecord.toString())
        mBinding?.radioButtonCosts?.isChecked = mViewModel.isCosts
        mBinding?.radioButtonCash?.isChecked = mViewModel.isCash
    }

    private fun onChangeSum() {
        val sumOfRecord: String = mBinding?.editTextSumOfOperation?.text.toString().trim()
        if (sumOfRecord.isNotEmpty() && sumOfRecord.isDigitsOnly()) {
            mViewModel.sumOfRecord = sumOfRecord.toInt()
            mBinding?.errorMsgSumOfMoney?.visibility = View.INVISIBLE
        } else
            mBinding?.errorMsgSumOfMoney?.visibility = View.VISIBLE


    }


}