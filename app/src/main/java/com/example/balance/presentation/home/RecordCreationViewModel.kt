package com.example.balance.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.EventComplete
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.Template
import com.example.balance.data.template.TemplateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val events = MutableLiveData<EventComplete>()

    init {
        viewModelScope.launch {
            updateLists().join()
            if (recordId != -1)
                applyValues()
            else
                onCostsSelected()
        }
    }

    companion object {
        private const val MAX_TEMPLATE_NAME_LENGTH = 15
        private const val MIN_TEMPLATE_NAME_LENGTH = 3
    }

    private fun updateLists(): Job {
        return viewModelScope.launch {
            val templates = withContext(Dispatchers.IO) {
                templateRepository.allTemplates.first()
                    .sortedBy { it.frequencyOfUse }
                    .reversed()
            }
            val costsCategories = withContext(Dispatchers.IO) {
                categoryRepository.allCostsCategory.first()
            }
            val profitCategories = withContext(Dispatchers.IO) {
                categoryRepository.allProfitCategory.first()
            }
            state.value = state.value?.copy(
                templates = templates,
                costsCategories = costsCategories,
                profitCategories = profitCategories
            )
        }
    }

    private fun applyValues() {
        viewModelScope.launch {
            val record = withContext(Dispatchers.IO) {
                recordRepository.getRecordById(recordId).first()
            }
            val recordCategory = withContext(Dispatchers.IO) {
                categoryRepository.getNameById(record.categoryId).first()
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
        val isCategoryValid = state.value?.selectedCategory?.isNotEmpty() == true
        val isAllValid =
            if (state.value?.isTemplate == true) {
                isSumValid && state.value?.isValidTemplateName == true && isCategoryValid
            } else isSumValid && isCategoryValid
        state.value = state.value?.copy(
            canSave = isAllValid
        )
    }

    fun getCategories(): Array<String> {
        val actualList: List<Category>? =
            when (state.value?.recordType) {
                RecordType.COSTS -> state.value?.costsCategories
                else -> state.value?.profitCategories
            }
        return actualList?.map { category -> category.name }?.toTypedArray()
            ?: arrayOf("")
    }

    fun getProfitCategoryNames(): List<String> =
        state.value?.profitCategories?.map { it.name } ?: listOf()

    fun getCostsCategoryNames(): List<String> =
        state.value?.costsCategories?.map { it.name } ?: listOf()

    fun onCategorySelected(category: String) {
        state.value = state.value?.copy(selectedCategory = category)
        checkAndSaveValid()
    }

    fun onChangeComment(newComment: String) {
        if (newComment != state.value?.comment) {
            saveCommentState(newComment)
        }
    }

    fun onChangeSum(sumRecord: String) {
        if (sumRecord != state.value?.sumRecord) {
            saveSumRecordState(sumRecord)
        }
    }

    fun onChangeTemplateName(newName: String) {
        if (newName != state.value?.templateName) {
            saveTemplateNameState(newName)
        }
    }

    fun onChangeImportantSwitch(isChecked: Boolean) {
        state.value = state.value?.copy(isImportant = isChecked)
    }

    fun onChangeTemplateSwitch(isChecked: Boolean) {
        state.value = state.value?.copy(isTemplate = isChecked)
        saveTemplateNameState(state.value?.templateName ?: "")
    }

    fun onCostsSelected() {
        val categories = state.value?.costsCategories
        state.value = state.value?.copy(recordType = RecordType.COSTS)
        onCategorySelected(
            if (categories?.isNotEmpty() == true) categories[0].name
            else ""
        )
    }

    fun onProfitSelected() {
        val categories = state.value?.profitCategories
        state.value = state.value?.copy(recordType = RecordType.PROFITS)
        onCategorySelected(
            if (categories?.isNotEmpty() == true) categories[0].name
            else ""
        )
    }

    fun onCashSelected() {
        state.value = state.value?.copy(moneyType = MoneyType.CASH)
    }

    fun onCardsSelected() {
        state.value = state.value?.copy(moneyType = MoneyType.CARDS)
    }

    fun onApplyTemplate(templatePosition: Int) {
        if (templatePosition != state.value?.selectedTemplatePosition) {
            state.value = state.value?.copy(selectedTemplatePosition = templatePosition)
        }
        val currentTemplate = state.value?.templates?.getOrNull(templatePosition - 1)
        if (currentTemplate != null) {
            viewModelScope.launch {
                val record = withContext(Dispatchers.IO) {
                    recordRepository.getRecordById(currentTemplate.recordId).first()
                }
                val recordCategory = withContext(Dispatchers.IO) {
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
                val recordId = withContext(Dispatchers.IO) {
                    recordRepository.insert(newRecord).toInt()
                }
                val templatePosition = state.value?.selectedTemplatePosition ?: 0
                val usedTemplate = state.value?.templates?.getOrNull(templatePosition - 1)
                if (usedTemplate != null) {
                    templateRepository.increaseUsage(usedTemplate.id)
                }
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

    fun onSaveNewCategory(
        categoryName: String,
        categoryType: CategoryType
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.insert(
                Category(
                    name = categoryName,
                    type = categoryType,
                    isDeleted = false
                )
            )
        }
        updateLists()
        val hasTypesMatchesProfit = categoryType == CategoryType.CATEGORY_PROFIT
                && state.value?.recordType == RecordType.PROFITS
        val hasTypesMatchesCosts = categoryType == CategoryType.CATEGORY_COSTS
                && state.value?.recordType == RecordType.COSTS
        if (hasTypesMatchesProfit || hasTypesMatchesCosts) {
            onCategorySelected(categoryName)
            checkAndSaveValid()
        }
    }

    fun onDefault() {
        state.value = state.value?.copy(
            selectedTemplatePosition = 0,
            sumRecord = "",
            recordType = RecordType.COSTS,
            moneyType = MoneyType.CASH,
            isImportant = false,
            isTemplate = false,
            templateName = "",
            isValidTemplateName = false,
            comment = "",
            canSave = false
        )

    }

}
