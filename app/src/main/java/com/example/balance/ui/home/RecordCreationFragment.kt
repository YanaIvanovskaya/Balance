package com.example.balance.ui.home

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.Template
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
            dataStore = BalanceApp.dataStore,
            recordId = args.currentRecord
        )
    }
    private lateinit var mNavController: NavController
    private val args: RecordCreationFragmentArgs by navArgs()

    private lateinit var mTemplatesSpinnerAdapter: ArrayAdapter<String>

    private var mEditTextSumMoney: EditText? = null
    private var mEditTextComment: EditText? = null
    private var mEditTextNameTemplate: EditText? = null
    private var mTemplatesSpinner: Spinner? = null

    private var mSumChangeListener: TextWatcher? = null
    private var mCommentChangeListener: TextWatcher? = null
    private var mTemplateNameChangeListener: TextWatcher? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecordCreationBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = findNavController()

        mViewModel.state.observe(viewLifecycleOwner, ::render)

        mViewModel.onCreateComplete = {
            mNavController.popBackStack()
        }

        initTextEdits()
        initButtons()
        initRadioButtons()
        initSwitches()
        applyChangesByArgs()
        return binding.root
    }

    private fun applyChangesByArgs() {
        if (args.currentRecord != -1) {
            mBinding?.toolbarNewRecord?.title = "Редактирование записи"

        }
    }



    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun renderEditText(state: RecordCreationState) {
        mEditTextSumMoney?.removeTextChangedListener(mSumChangeListener)
        mEditTextSumMoney?.setText(state.sumRecord)
        mSumChangeListener = mEditTextSumMoney?.doAfterTextChanged {
            mViewModel.onChangeSum(it.toString())
            mEditTextSumMoney?.setSelection(it.toString().length)
        }
        mEditTextNameTemplate?.removeTextChangedListener(mTemplateNameChangeListener)
        mEditTextNameTemplate?.setText(state.templateName)
        mTemplateNameChangeListener = mEditTextNameTemplate?.doAfterTextChanged {
            mViewModel.onChangeTemplateName(it.toString())
            mEditTextNameTemplate?.setSelection(it.toString().length)
        }
        mEditTextComment?.removeTextChangedListener(mCommentChangeListener)
        mEditTextComment?.setText(state.comment)
        mCommentChangeListener = mEditTextComment?.doAfterTextChanged {
            mViewModel.onChangeComment(it.toString())
            mEditTextComment?.setSelection(it.toString().length)
        }
    }

    private fun render(state: RecordCreationState) {
        renderEditText(state)
        mBinding?.spinnerTemplates?.setSelection(state.selectedTemplatePosition)
        mBinding?.errorMsgSumOfMoney?.isVisible = state.sumRecord.isEmpty()

        when (state.recordType) {
            RecordType.COSTS -> mBinding?.radioButtonCosts?.isChecked = true
            RecordType.PROFITS -> mBinding?.radioButtonProfits?.isChecked = true
        }
        when (state.moneyType) {
            MoneyType.CASH -> mBinding?.radioButtonCash?.isChecked = true
            MoneyType.CARDS -> mBinding?.radioButtonCards?.isChecked = true
        }

        mBinding?.chipCategory?.text = state.selectedCategory
        mBinding?.switchIsImportantRecord?.isChecked = state.isImportant
        mBinding?.switchIsTemplate?.isChecked = state.isTemplate
        mBinding?.editTextNameTemplate?.isVisible = state.isTemplate
        mBinding?.errorMsgTemplateName?.isVisible = state.isTemplate && !state.isValidTemplateName

        mBinding?.buttonCreateAndSaveNewRecord?.isEnabled = state.canSave
        renderTemplates(state.templates)
    }

    private fun renderTemplates(templates: List<Template>) {
        mTemplatesSpinner = mBinding?.spinnerTemplates
        mTemplatesSpinnerAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            templates.map { it.name }
                .toMutableList()
                .apply { add(0, "Без шаблона") }
        )
        mTemplatesSpinnerAdapter
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mTemplatesSpinner?.adapter = mTemplatesSpinnerAdapter
        mTemplatesSpinner?.onItemSelectedListener = TemplateSelectedListener(mViewModel)

    }

    private fun initTextEdits() {
        mEditTextSumMoney = mBinding?.editTextSumMoney
        mEditTextNameTemplate = mBinding?.editTextNameTemplate
        mEditTextComment = mBinding?.editTextComment
    }

    private fun initRadioButtons() {
        mBinding?.radioButtonCash?.setOnCheckedChangeListener { btn, isChecked ->
            if (btn.isShown && isChecked) mViewModel.onCashSelected()
        }

        mBinding?.radioButtonCards?.setOnCheckedChangeListener { btn, isChecked ->
            if (btn.isShown && isChecked) mViewModel.onCardsSelected()
        }

        mBinding?.radioButtonCosts?.setOnCheckedChangeListener { btn, isChecked ->
            if (btn.isShown && isChecked) mViewModel.onCostsSelected()
        }

        mBinding?.radioButtonProfits?.setOnCheckedChangeListener { btn, isChecked ->
            if (btn.isShown && isChecked) mViewModel.onProfitSelected()
        }
    }

    private fun initButtons() {
        mBinding?.buttonChangeCategory?.setOnClickListener {
            mViewModel.showCategoryDialog(requireContext())
        }

        mBinding?.buttonCreateAndSaveNewRecord?.setOnClickListener {
            mViewModel.onSaveRecord()
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

    private class TemplateSelectedListener(
        private val viewModel: RecordCreationViewModel
    ) : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position != 0)
                viewModel.onApplyTemplate(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}

    }

}