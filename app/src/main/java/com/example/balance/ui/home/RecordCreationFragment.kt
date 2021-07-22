package com.example.balance.ui.home

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
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
            templateRepository = BalanceApp.templateRepository,
            dataStore = BalanceApp.dataStore
        )
    }
    private lateinit var mNavController: NavController
    private var mSumChangeListener: TextWatcher? = null
    private var mCommentChangeListener: TextWatcher? = null
    private lateinit var mTemplatesSpinnerAdapter: ArrayAdapter<String>
    private var mTemplatesSpinner: Spinner? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecordCreationBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = findNavController()

        mViewModel.state.observe(viewLifecycleOwner, ::render)

        mViewModel.profitCategories.observe(viewLifecycleOwner, {
            mViewModel.updateProfitCategory()
        })

        mViewModel.costsCategories.observe(viewLifecycleOwner, {
            mViewModel.updateCostsCategory()
        })

        mViewModel.templates.observe(viewLifecycleOwner, {
            mViewModel.updateTemplates()
            initTemplateSpinner()
        })

        initTemplateSpinner()
        initButtons()
        initRadioButtons()
        initSwitches()

        return binding.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun render(state: RecordCreationState) {

        mBinding?.spinnerTemplates?.setSelection(state.selectedTemplatePosition)

        mBinding?.editTextSumMoney?.removeTextChangedListener(mSumChangeListener)
        mBinding?.editTextSumMoney?.setText(state.sumRecord)
        mSumChangeListener = mBinding?.editTextSumMoney?.doAfterTextChanged {
            mViewModel.onChangeSum(it.toString())
        }

        mBinding?.editTextComment?.removeTextChangedListener(mCommentChangeListener)
        mBinding?.editTextComment?.setText(state.comment)
        mCommentChangeListener = mBinding?.editTextComment?.doAfterTextChanged {
            mViewModel.onChangeComment(it.toString())
        }

        mBinding?.errorMsgSumOfMoney?.isVisible = !state.canSave

        when (state.recordType) {
            RecordType.COSTS -> mBinding?.radioButtonCosts?.isChecked = true
            RecordType.PROFITS -> mBinding?.radioButtonProfits?.isChecked = true
        }
        when (state.moneyType) {
            MoneyType.CASH -> mBinding?.radioButtonCash?.isChecked = true
            MoneyType.CARDS -> mBinding?.radioButtonCards?.isChecked = true
        }

        mBinding?.chipCategory?.text = state.selectedCategory
        println(state.selectedCategory)

        mBinding?.switchIsImportantRecord?.isChecked = state.isImportant

        mBinding?.switchIsTemplate?.isChecked = state.isTemplate

        mBinding?.editTextNameTemplate?.isVisible = state.isTemplate


        mBinding?.buttonCreateAndSaveNewRecord?.isEnabled = state.canSave

    }

    private fun initTemplateSpinner() {
        mTemplatesSpinner = mBinding?.spinnerTemplates
        mTemplatesSpinnerAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item, mViewModel.getTemplates()
        )
        mTemplatesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        mTemplatesSpinner?.adapter = mTemplatesSpinnerAdapter

        mTemplatesSpinner?.onItemSelectedListener = SpinnerItemSelectedListener()

    }

    private fun initRadioButtons() {
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
    }

    private fun initButtons() {
        mBinding?.buttonChangeCategory?.setOnClickListener {
            mViewModel.showCategoryDialog(requireContext())
        }
        mBinding?.buttonCreateAndSaveNewRecord?.setOnClickListener {
            mViewModel.onSaveRecord()
            mNavController.popBackStack()
        }

        mBinding?.toolbarNewRecord?.setNavigationOnClickListener {
            mNavController.popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mNavController.popBackStack()
                }
            }
        )
    }

    private fun initSwitches() {
        mBinding?.switchIsImportantRecord?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isShown)
                mViewModel.onChangeImportantSwitch(isChecked)
        }

        mBinding?.switchIsTemplate?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isShown)
                mViewModel.onChangeTemplateSwitch(isChecked)
        }
    }


    class SpinnerItemSelectedListener : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            println("onItemSelected $position")
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            println("onNothingSelected")
        }

    }

}