package com.example.balance.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.balance.BalanceApp
import com.example.balance.R
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.RecordType
import com.example.balance.databinding.FragmentRecordCreationBinding
import com.example.balance.presentation.home.RecordCreationState
import com.example.balance.presentation.home.RecordCreationViewModel
import com.example.balance.presentation.getViewModel
import com.example.balance.toUpperFirst
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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
    private var mEditTextSumMoney: EditText? = null
    private var mEditTextComment: EditText? = null
    private var mEditTextNameTemplate: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecordCreationBinding.inflate(inflater, container, false)
        mBinding = binding
        mNavController = findNavController()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.state.observe(viewLifecycleOwner, ::render)
        mViewModel.events.observe(viewLifecycleOwner) { event ->
            event.consume { isSavingCompleted ->
                if (isSavingCompleted) {
                    mNavController.popBackStack()
                    Toast.makeText(
                        requireContext(),
                        "Сохранено",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        initWidgets()
        initTextEdits()
        initButtons()
        initRadioButtons()
        initSwitches()
        applyChangesByArgs()
    }

    private fun applyChangesByArgs() {
        val currentRecord = args.currentRecord
        if (currentRecord != -1) {
            mBinding?.toolbarNewRecord?.title = "Редактирование записи"
            mBinding?.buttonCreateAndSaveNewRecord?.text = "Сохранить изменения"
            mBinding?.switchIsTemplate?.isVisible = false
            mBinding?.switchIsImportantRecord?.isVisible = false
            mBinding?.horizontalScrollView?.visibility = View.GONE
            mBinding?.extraConstraintLayout?.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun render(state: RecordCreationState) {
        renderTemplates(state)
        mBinding?.editTextSumMoney?.setText(state.sumRecord)
        mBinding?.editTextNameTemplate?.setText(state.templateName)
        mBinding?.editTextComment?.setText(state.comment)
        when (state.recordType) {
            RecordType.COSTS -> mBinding?.radioButtonCosts?.isChecked = true
            else -> mBinding?.radioButtonProfits?.isChecked = true
        }
        when (state.moneyType) {
            MoneyType.CASH -> mBinding?.radioButtonCash?.isChecked = true
            else -> mBinding?.radioButtonCards?.isChecked = true
        }
        mBinding?.categoryName?.setText(state.selectedCategory.toUpperFirst())
        mBinding?.switchIsImportantRecord?.isChecked = state.isImportant
        mBinding?.switchIsTemplate?.isChecked = state.isTemplate
        mBinding?.nameTemplateTextInputLayout?.isVisible = state.isTemplate
        mBinding?.buttonCreateAndSaveNewRecord?.isEnabled = state.canSave
    }

    private fun renderTemplates(state: RecordCreationState) {
        val templates = state.templates
        val templateNames =
            templates.map { it.name }
                .toMutableList()
                .apply {
                    add(0, "Без шаблона")
                }
        if (templateNames.size == 1) {
            return
        }
        val chipGroup = mBinding?.templatesGroup
        if (templateNames.size > chipGroup?.size ?: 0) {
            templateNames.forEachIndexed { index, s ->
                val nullParent: ViewGroup? = null
                val view = layoutInflater.inflate(R.layout.view_chip_checkable, nullParent, false)
                if (view is Chip) {
                    view.text = s
                    view.isCheckable = true
                    if (index == 0) {
                        view.isChecked = true
                    } else {
                        view.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                mViewModel.onApplyTemplate(index)
                            }
                        }
                    }
                    chipGroup?.addView(view)
                }
            }
        }
        val checkedChip: Chip = chipGroup?.get(state.selectedTemplatePosition) as Chip
        checkedChip.isChecked = true
    }

    private fun initTextEdits() {
        mEditTextSumMoney = mBinding?.editTextSumMoney
        mEditTextNameTemplate = mBinding?.editTextNameTemplate
        mEditTextComment = mBinding?.editTextComment
        mEditTextSumMoney?.doAfterTextChanged {
            mViewModel.onChangeSum(it.toString())
            mEditTextSumMoney?.setSelection(it.toString().length)
        }
        mEditTextNameTemplate?.doAfterTextChanged {
            mViewModel.onChangeTemplateName(it.toString())
            mEditTextNameTemplate?.setSelection(it.toString().length)
        }
        mEditTextComment?.doAfterTextChanged {
            mViewModel.onChangeComment(it.toString())
            mEditTextComment?.setSelection(it.toString().length)
        }
    }

    private fun initRadioButtons() {
        mBinding?.radioButtonCash?.setOnCheckedChangeListener { btn, isChecked ->
            if (btn.isShown && isChecked) {
                mViewModel.onCashSelected()
            }
        }
        mBinding?.radioButtonCards?.setOnCheckedChangeListener { btn, isChecked ->
            if (btn.isShown && isChecked) {
                mViewModel.onCardsSelected()
            }
        }
        mBinding?.radioButtonCosts?.setOnCheckedChangeListener { btn, isChecked ->
            if (btn.isShown && isChecked) {
                mViewModel.onCostsSelected()
            }
        }
        mBinding?.radioButtonProfits?.setOnCheckedChangeListener { btn, isChecked ->
            if (btn.isShown && isChecked) {
                mViewModel.onProfitSelected()
            }
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
            if (isChecked)
                scrollDown()
        }
    }

    private fun scrollDown() {
        mBinding?.scrollView2?.post { mBinding?.scrollView2?.fullScroll(View.FOCUS_DOWN) }
    }

    private fun initWidgets() {
        mBinding?.extraConstraintLayout?.setOnClickListener {
            val imageDownArrow =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_down_arrow, null)
            val imageUpArrow =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_up_arrow, null)
            if (mBinding?.switchIsTemplate?.isVisible == true) {
                mBinding?.switchIsTemplate?.visibility = View.GONE
                mBinding?.nameTemplateTextInputLayout?.visibility = View.GONE
                mBinding?.switchIsImportantRecord?.visibility = View.GONE
                mBinding?.extraArrowImageView?.setImageDrawable(imageDownArrow)
            } else {
                mBinding?.switchIsTemplate?.visibility = View.VISIBLE
                mBinding?.nameTemplateTextInputLayout?.visibility =
                    if (mBinding?.switchIsTemplate?.isChecked == true) View.VISIBLE
                    else View.GONE
                mBinding?.switchIsImportantRecord?.visibility = View.VISIBLE
                mBinding?.extraArrowImageView?.setImageDrawable(imageUpArrow)
                scrollDown()
            }
        }
    }

    private fun showChangeCategoryBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
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
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    mViewModel.onCategorySelected(categories[position])
                    bottomSheetDialog.dismiss()
                }
            listView?.onItemClickListener = onItemClickListener
            listView?.adapter = adapter
        }
        bottomSheetDialog.show()
    }

    private fun showAddCategoryBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialog )
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_category_creation)
        val profitCategoryNames = mViewModel.getProfitCategoryNames()
        val costsCategoryNames = mViewModel.getCostsCategoryNames()
        val categoryCosts =
            bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton_category_costs)
        val categoryProfit =
            bottomSheetDialog.findViewById<RadioButton>(R.id.radioButton_category_profit)
        val categoryName =
            bottomSheetDialog.findViewById<TextInputEditText>(R.id.text_category_name)
        val buttonSave = bottomSheetDialog.findViewById<Button>(R.id.button_save_category)
        val textInputLayout =
            bottomSheetDialog.findViewById<TextInputLayout>(R.id.text_input_layout)
        when (mViewModel.state.value?.recordType) {
            RecordType.PROFITS -> categoryProfit?.isChecked = true
            RecordType.COSTS -> categoryCosts?.isChecked = true
        }
        var currentCategoryName = ""
        categoryName?.doAfterTextChanged {
            currentCategoryName = it.toString().trim()
            val alreadyInCosts =
                categoryCosts?.isChecked == true && it.toString().trim() in costsCategoryNames
            val alreadyInProfits =
                categoryProfit?.isChecked == true && it.toString().trim() in profitCategoryNames
            if (alreadyInCosts || alreadyInProfits) {
                textInputLayout?.helperText = "Такая категория уже существует"
                buttonSave?.isEnabled = false
            } else if (categoryName.text?.trim()?.isEmpty() == true) {
                textInputLayout?.helperText = "Пустое название категории"
                buttonSave?.isEnabled = false
            } else {
                textInputLayout?.helperText = " "
                buttonSave?.isEnabled = true
            }
        }
        buttonSave?.setOnClickListener {
            val type = when (categoryCosts?.isChecked ?: false) {
                true -> CategoryType.CATEGORY_COSTS
                false -> CategoryType.CATEGORY_PROFIT
            }
            mViewModel.onSaveNewCategory(currentCategoryName, type)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

}