package com.example.balance.presentation

import android.content.Context
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.*
import com.example.balance.EventComplete
import com.example.balance.R
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.Template
import com.example.balance.data.template.TemplateRepository
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class RecordCreationState(
    val selectedTemplatePosition: Int,
    val templates: List<Template>,
    val costsCategories: List<Category>,
    val profitCategories: List<Category>,
    val sumRecord: String,
    val recordType: RecordType,
    val moneyType: MoneyType,
    val selectedCategory: String,
    val isImportant: Boolean,
    val isTemplate: Boolean,
    val templateName: String,
    val isValidTemplateName: Boolean,
    val comment: String,
    val canSave: Boolean
) {

    companion object {
        fun default() = RecordCreationState(
            selectedTemplatePosition = 0,
            templates = listOf(),
            costsCategories = listOf(),
            profitCategories = listOf(),
            sumRecord = "",
            recordType = RecordType.COSTS,
            moneyType = MoneyType.CASH,
            selectedCategory = "",
            isImportant = false,
            isTemplate = false,
            templateName = "",
            isValidTemplateName = false,
            comment = "",
            canSave = false
        )
    }

}

class RecordCreationViewModel(
    private val recordRepository: RecordRepository,
    private val categoryRepository: CategoryRepository,
    private val templateRepository: TemplateRepository,
    private val recordId: Int
) : ViewModel() {

    val state = MutableLiveData(RecordCreationState.default())

    val events = MutableLiveData<EventComplete<Boolean>>()

    init {
        updateLists()
    }

    private fun updateLists() {
        viewModelScope.launch {
            val templates =
                withContext(Dispatchers.IO) { templateRepository.allTemplates.first() }
            val costsCategories =
                withContext(Dispatchers.IO) { categoryRepository.allCostsCategory.first() }
            val profitCategories =
                withContext(Dispatchers.IO) { categoryRepository.allProfitCategory.first() }

            state.value = state.value?.copy(
                templates = templates,
                costsCategories = costsCategories,
                profitCategories = profitCategories
            )

            if (recordId != -1)
                applyValues()
            else
                onCostsSelected()

        }
    }

    private fun applyValues() {
        if (recordId != -1) {
            viewModelScope.launch {

                val record =
                    withContext(Dispatchers.IO) { recordRepository.getRecordById(recordId).first() }

                val recordCategory =
                    try {
                        withContext(Dispatchers.IO) {
                            categoryRepository.getNameById(record.categoryId).first()
                        }
                    } catch (e: NoSuchElementException) {
                        "Deleted"
                    }

                state.value = state.value?.copy(
                    sumRecord = record.sumOfMoney.toString(),
                    recordType = record.recordType,
                    moneyType = record.moneyType,
                    selectedCategory = recordCategory,
                    comment = record.comment,
                    canSave = true
                )
                onCategorySelected(recordCategory)
            }
        }
    }

    private fun saveSumRecordState(newSumRecord: String) {
        state.value = state.value?.copy(sumRecord = newSumRecord)
        checkAndSaveValid()
    }

    private fun saveCommentState(newComment: String) {
        state.value = state.value?.copy(comment = newComment)
    }

    private fun saveTemplateNameState(newName: String) {
        state.value = state.value?.copy(
            templateName = newName,
            isValidTemplateName =
            newName.length in MIN_TEMPLATE_NAME_LENGTH..MAX_TEMPLATE_NAME_LENGTH
        )
        checkAndSaveValid()
    }

    private fun checkAndSaveValid() {
        val isSumValid = state.value?.sumRecord?.isNotEmpty() == true
        val isAllValid = if (state.value?.isTemplate == true) {
            isSumValid && state.value?.isValidTemplateName == true
        } else isSumValid
        state.value = state.value?.copy(
            canSave = isAllValid
        )
    }

    private fun getCategories(): Array<String> {
        val actualList: List<Category>? =
            when (state.value?.recordType) {
                RecordType.COSTS -> state.value?.costsCategories
                else -> state.value?.profitCategories
            }
        return actualList?.map { category -> category.name }?.toTypedArray()
            ?: arrayOf("")
    }

    private fun onCategorySelected(category: String) {
        state.value = state.value?.copy(selectedCategory = category)
    }

    fun showCategorySelectionDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Выберите категорию")
        val categories = getCategories()
        val checkedItem = 0
        builder.setSingleChoiceItems(categories, checkedItem) { dialog, which ->
            onCategorySelected(categories[which])
            dialog.cancel()
        }
        builder.setNegativeButton("Закрыть", null)

        val dialog = builder.create()
        dialog.show()
    }


    fun showCategoryCreationDialog(context: Context) {
        val profitCategoryNames =
            state.value?.profitCategories?.map {
                it.name
            } ?: listOf()
        val costsCategoryNames =
            state.value?.costsCategories?.map {
                it.name
            } ?: listOf()

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Новая категория")
       builder.setView( R.layout.fragment_dialog_category_creation)

        val dialog = builder.create()
        dialog.setContentView(R.layout.fragment_dialog_category_creation)
        val categoryCosts = dialog.findViewById<RadioButton>(R.id.radioButton_category_costs)
        val categoryProfit = dialog.findViewById<RadioButton>(R.id.radioButton_category_profit)
        val categoryName = dialog.findViewById<TextInputEditText>(R.id.text_category_name)
        val errorText = dialog.findViewById<TextView>(R.id.error_category_creation)
        val buttonSave = dialog.findViewById<Button>(R.id.button_save_category)
        val buttonClose = dialog.findViewById<Button>(R.id.button_close_dialog)
        dialog.show()
        categoryCosts?.isSelected = true

        categoryName?.doAfterTextChanged {
            println(it.toString())
            if ((categoryCosts?.isChecked == true && it.toString() in costsCategoryNames) ||
                (categoryProfit?.isChecked == true && it.toString() in profitCategoryNames)
            ) {
                errorText?.isVisible = true
                buttonSave?.isEnabled = false

            } else if (categoryName.text?.isEmpty() == true) {
                errorText?.isVisible = false
                buttonSave?.isEnabled = false
            } else {
                errorText?.isVisible = false
                buttonSave?.isEnabled = true
            }
        }

        buttonClose?.setOnClickListener {
            println("buttonClose")
            dialog.cancel()
        }

        buttonSave?.setOnClickListener {
            viewModelScope.launch(Dispatchers.IO) {
                categoryRepository.insert(
                    Category(
                        name = categoryName?.text.toString(),
                        type = when (categoryCosts?.isChecked) {
                            true -> CategoryType.CATEGORY_COSTS
                            else -> CategoryType.CATEGORY_PROFIT
                        },
                        isDeleted = false
                    )
                )
            }
            dialog.cancel()
        }


    }


    fun onChangeComment(newComment: String) = saveCommentState(newComment)

    fun onChangeSum(sumRecord: String) = saveSumRecordState(sumRecord)

    fun onChangeTemplateName(newName: String) = saveTemplateNameState(newName)

    fun onChangeImportantSwitch(isChecked: Boolean) {
        state.value = state.value?.copy(isImportant = isChecked)
    }

    fun onChangeTemplateSwitch(isChecked: Boolean) {
        state.value = state.value?.copy(isTemplate = isChecked)
        saveTemplateNameState(state.value?.templateName ?: "")
    }

    fun onCostsSelected() {
        val categories = state.value?.costsCategories
        state.value = state.value?.copy(
            recordType = RecordType.COSTS,
            selectedCategory = if (categories?.isNotEmpty() == true) categories[0].name else ""
        )
    }

    fun onProfitSelected() {
        val categories = state.value?.profitCategories
        state.value = state.value?.copy(
            recordType = RecordType.PROFITS,
            selectedCategory = if (categories?.isNotEmpty() == true) categories[0].name else ""
        )
    }

    fun onCashSelected() {
        state.value = state.value?.copy(moneyType = MoneyType.CASH)
    }

    fun onCardsSelected() {
        state.value = state.value?.copy(moneyType = MoneyType.CARDS)
    }

    fun onApplyTemplate(templatePosition: Int) {
        state.value = state.value?.copy(selectedTemplatePosition = templatePosition)
        val currentTemplate = state.value?.templates?.getOrNull(templatePosition - 1)
        if (currentTemplate != null) {
            viewModelScope.launch {
                val record = withContext(Dispatchers.IO) {
                    recordRepository.getRecordById(currentTemplate.recordId).first()
                }
                val recordCategory =
                    withContext(Dispatchers.IO) {
                        categoryRepository.getNameById(record.categoryId).first()
                    }


                if (record.recordType == RecordType.COSTS)
                    onCostsSelected()
                else
                    onProfitSelected()
                state.value = state.value?.copy(
                    sumRecord = record.sumOfMoney.toString(),
                    recordType = record.recordType,
                    moneyType = record.moneyType,
                    selectedCategory = recordCategory,
                    canSave = true
                )
            }
        }
    }

    fun onSaveOrEditRecord() {

//        runBlocking { categoryRepository.deleteAll() }

        viewModelScope.launch {
            val sumMoney = Integer.parseInt(state.value?.sumRecord ?: "0")
            val recordType = state.value?.recordType ?: RecordType.COSTS
            val moneyType = state.value?.moneyType ?: MoneyType.CASH
            val categoryId: Int = withContext(Dispatchers.IO) {
                val categoryName = state.value?.selectedCategory ?: ""
                categoryRepository.getId(categoryName)
            }
            val comment = state.value?.comment ?: ""

            if (recordId == -1) {
                val newRecord = Record(
                    sumOfMoney = sumMoney,
                    categoryId = categoryId,
                    recordType = recordType,
                    moneyType = moneyType,
                    isImportant = state.value?.isImportant ?: false,
                    comment = comment
                )
                val recordId =
                    withContext(Dispatchers.IO) { recordRepository.insert(newRecord).toInt() }

                if (state.value?.isTemplate == true) {
                    val newTemplate = Template(
                        name = state.value?.templateName ?: "",
                        recordId = recordId
                    )
                    withContext(Dispatchers.IO) { templateRepository.insert(newTemplate) }
                }
                events.value = EventComplete(isComplete = true)

            } else {
                withContext(Dispatchers.IO) {
                    recordRepository.update(
                        recordId = recordId,
                        sumOfMoney = sumMoney,
                        recordType = recordType,
                        moneyType = moneyType,
                        categoryId = categoryId,
                        comment = comment
                    )
                }
                events.value = EventComplete(isComplete = true)
            }
        }

    }

    companion object {
        private const val MAX_TEMPLATE_NAME_LENGTH = 15
        private const val MIN_TEMPLATE_NAME_LENGTH = 3
    }

}
