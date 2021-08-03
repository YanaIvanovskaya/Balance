package com.example.balance.ui.home

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.Template
import com.example.balance.databinding.FragmentRecordCreationBinding
import com.example.balance.presentation.RecordCreationState
import com.example.balance.presentation.RecordCreationViewModel
import com.example.balance.presentation.getViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RecordCreationFragment : Fragment(R.layout.fragment_record_creation) {

    private var mBinding: FragmentRecordCreationBinding? = null
    private val mViewModel: RecordCreationViewModel by getViewModel {
        RecordCreationViewModel(
            recordRepository = BalanceApp.recordRepository,
            categoryRepository = BalanceApp.categoryRepository,
            templateRepository = BalanceApp.templateRepository,
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
        mViewModel.events.observe(viewLifecycleOwner) { event ->
            event.consume { isSavingCompleted ->
                if (isSavingCompleted) {
                    mNavController.popBackStack()
                    Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
                }
            }
        }
        initTextEdits()
        initButtons()
        initRadioButtons()
        initSwitches()
        applyChangesByArgs()
        return binding.root
    }

    private fun applyChangesByArgs() {
        val currentRecord = args.currentRecord
        if (currentRecord != -1) {
            mBinding?.toolbarNewRecord?.title = "Редактирование записи"
            mBinding?.spinnerTemplates?.visibility = View.GONE
            mBinding?.buttonCreateAndSaveNewRecord?.text = "Сохранить изменения"
            mBinding?.switchIsTemplate?.isVisible = false
            mBinding?.switchIsImportantRecord?.isVisible = false
            mBinding?.hintTemplate?.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        mBinding = null
        mSumChangeListener = null
        mCommentChangeListener = null
        mTemplateNameChangeListener = null
        super.onDestroyView()
    }

    private fun renderEditText(state: RecordCreationState) {
        mEditTextSumMoney?.removeTextChangedListener(mSumChangeListener)
        mEditTextSumMoney?.text?.clear()
        mEditTextSumMoney?.append(state.sumRecord)
        mSumChangeListener = mEditTextSumMoney?.doAfterTextChanged {
            mViewModel.onChangeSum(it.toString())
            mEditTextSumMoney?.setSelection(it.toString().length)
        }
        mEditTextNameTemplate?.removeTextChangedListener(mTemplateNameChangeListener)
        mEditTextNameTemplate?.text?.clear()
        mEditTextNameTemplate?.append(state.templateName)
        mTemplateNameChangeListener = mEditTextNameTemplate?.doAfterTextChanged {
            mViewModel.onChangeTemplateName(it.toString())
            mEditTextNameTemplate?.setSelection(it.toString().length)
        }
        mEditTextComment?.removeTextChangedListener(mCommentChangeListener)
        mEditTextComment?.text?.clear()
        mEditTextComment?.append(state.comment)
        mCommentChangeListener = mEditTextComment?.doAfterTextChanged {
            mViewModel.onChangeComment(it.toString())
            mEditTextComment?.setSelection(it.toString().length)
        }
    }

    private fun render(state: RecordCreationState) {
        renderEditText(state)
        renderTemplates(state.templates)
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
    }

    private fun renderTemplates(templates: List<Template>) {
        mTemplatesSpinner = mBinding?.spinnerTemplates
        mTemplatesSpinnerAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            templates.sortedBy { it.frequencyOfUse }
                .map { it.name }
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
            showChangeCategoryBottomSheet()
        }

        mBinding?.buttonAddCategory?.setOnClickListener {
            showAddCategoryBottomSheet()
        }

        mBinding?.buttonCreateAndSaveNewRecord?.setOnClickListener {
            mViewModel.onSaveOrEditRecord()
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

    private fun showChangeCategoryBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_category_selection)
        val listView = bottomSheetDialog.findViewById<ListView>(R.id.list_view)

        val categories = mViewModel.getCategories()

        if (categories.isEmpty()) {
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_no_categories)
            val buttonAddCategory =
                bottomSheetDialog.findViewById<Button>(R.id.button_add_first_category)
            buttonAddCategory?.setOnClickListener {
                bottomSheetDialog.dismiss()
                showAddCategoryBottomSheet()
            }
        } else {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1, categories
            )
            val onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    mViewModel.onCategorySelected(categories[position])
                    bottomSheetDialog.dismiss()
                }

            listView?.onItemClickListener = onItemClickListener
            listView?.adapter = adapter
        }
        bottomSheetDialog.show()
    }

    private fun showAddCategoryBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_category_creation)

        val profitCategoryNames = mViewModel.getProfitCategoryNames()
        val costsCategoryNames = mViewModel.getCostsCategoryNames()

        val categoryCosts =
            bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton_category_costs)
        val categoryProfit =
            bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton_category_profit)
        val categoryName =
            bottomSheetDialog.findViewById<TextInputEditText>(R.id.text_category_name)
        val errorText = bottomSheetDialog.findViewById<TextView>(R.id.error_category_creation)
        val buttonSave = bottomSheetDialog.findViewById<Button>(R.id.button_save_category)

        when (mViewModel.state.value?.recordType) {
            RecordType.PROFITS -> categoryProfit?.isChecked = true
            RecordType.COSTS -> categoryCosts?.isChecked = true
        }

        var currentCategoryName = ""
        categoryName?.doAfterTextChanged {
            currentCategoryName = it.toString().trim()
            if ((categoryCosts?.isChecked == true && it.toString().trim() in costsCategoryNames) ||
                (categoryProfit?.isChecked == true && it.toString().trim() in profitCategoryNames)
            ) {
                errorText?.isVisible = true
                buttonSave?.isEnabled = false

            } else if (categoryName.text?.trim()?.isEmpty() == true) {
                errorText?.isVisible = false
                buttonSave?.isEnabled = false
            } else {
                errorText?.isVisible = false
                buttonSave?.isEnabled = true
            }
        }

        buttonSave?.setOnClickListener {
            val type = when (categoryCosts?.isChecked ?: false) {
                true -> CategoryType.CATEGORY_COSTS
                false -> CategoryType.CATEGORY_PROFIT
            }
            mViewModel.onSaveNewCategory(currentCategoryName,type)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }


    private class TemplateSelectedListener(
        private val viewModel: RecordCreationViewModel
    ) : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.onApplyTemplate(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}

    }

}