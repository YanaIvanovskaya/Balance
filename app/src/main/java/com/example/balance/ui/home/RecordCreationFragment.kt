package com.example.balance.ui.home

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.data.MoneyType
import com.example.balance.data.RecordType
import com.example.balance.databinding.FragmentRecordCreationBinding
import com.example.balance.presentation.RecordCreationState
import com.example.balance.presentation.RecordCreationViewModel
import com.example.balance.presentation.getViewModel

class RecordCreationFragment : Fragment(R.layout.fragment_record_creation) {

    private var mBinding: FragmentRecordCreationBinding? = null
    private val mViewModel: RecordCreationViewModel by getViewModel {
        RecordCreationViewModel(
            recordRepository = BalanceApp.recordRepository,
            categoryRepository = BalanceApp.categoryRepository,
            dataStore = BalanceApp.dataStore
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
        when (state.recordType) {
            RecordType.COSTS -> mBinding?.radioButtonCosts?.isChecked = true
            RecordType.PROFITS -> mBinding?.radioButtonProfits?.isChecked = true
        }
        when (state.moneyType) {
            MoneyType.CASH -> mBinding?.radioButtonCash?.isChecked = true
            MoneyType.CARDS -> mBinding?.radioButtonCards?.isChecked = true
        }
        mBinding?.buttonCreateAndSaveNewRecord?.isEnabled = state.canSave
        mBinding?.errorMsgSumOfMoney?.isVisible = !state.canSave
        mBinding?.chipCategory?.text = state.selectedCategory

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
        mBinding?.radioButtonCash?.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (compoundButton.isShown && isChecked)
                mViewModel.onCashSelected()
        }
        mBinding?.radioButtonCards?.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (compoundButton.isShown && isChecked)
                mViewModel.onCardsSelected()
        }

        mBinding?.radioButtonCosts?.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (compoundButton.isShown && isChecked)
                mViewModel.onCostsSelected()
        }

        mBinding?.radioButtonProfits?.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (compoundButton.isShown && isChecked)
                mViewModel.onProfitSelected()
        }

        mBinding?.buttonChangeCategory?.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Выберите категорию")
            val categories = mViewModel.getCategories()
            println(categories)
            val checkedItem = 0
            builder.setSingleChoiceItems(categories, checkedItem) { dialog, which ->
                mViewModel.onCategorySelected(categories[which])
                dialog.cancel()
            }
            builder.setNegativeButton("Cancel", null)
            val dialog = builder.create()
            dialog.show()
        }

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