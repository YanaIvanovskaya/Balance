package com.example.balance.presentation

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.*
import com.example.balance.Event
import com.example.balance.data.*
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.record.MoneyType
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.Template
import com.example.balance.data.template.TemplateRepository
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
    private val templateRepository: TemplateRepository
) : ViewModel() {

    val state = MutableLiveData(RecordCreationState.default())

    init {
        templateRepository.allTemplates
            .onEach { newTemplates: List<Template> ->
                state.value = state.value?.copy(
                    templates = newTemplates
                )
            }
            .launchIn(scope = viewModelScope)

        categoryRepository.allCostsCategory
            .onEach { newCostsCategories: List<Category> ->
                state.value = state.value?.copy(
                    costsCategories = newCostsCategories
                )
                if (state.value?.recordType == RecordType.COSTS) {
                    state.value = state.value?.copy(
                        selectedCategory = newCostsCategories[0].name
                    )
                }
            }
            .launchIn(scope = viewModelScope)

        categoryRepository.allProfitCategory
            .onEach { newProfitCategories: List<Category> ->
                state.value = state.value?.copy(
                    profitCategories = newProfitCategories
                )
                if (state.value?.recordType == RecordType.PROFITS) {
                    state.value = state.value?.copy(
                        selectedCategory = newProfitCategories[0].name
                    )
                }
            }
            .launchIn(scope = viewModelScope)
    }


    fun applyValues(recordId: Int) {
        if (recordId != -1) {
            val record = runBlocking { recordRepository.getRecordById(recordId).first() }
            state.value = state.value?.copy(
                sumRecord = record.sumOfMoney.toString(),
                recordType = record.recordType,
                moneyType = record.moneyType,
                selectedCategory = record.category,
                comment = record.comment,
                canSave = true
            )
            println("applyValues ${record.category}")
        }
    }


    private fun saveSumRecordState(newSumRecord: String) {
        state.value = state.value?.copy(
            sumRecord = newSumRecord
        )
        checkAndSaveValid()
    }

    private fun saveCommentState(newComment: String) {
        state.value = state.value?.copy(
            comment = newComment
        )
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
        state.value = state.value?.copy(
            selectedCategory = category
        )
    }

    fun showCategoryDialog(context: Context) {
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

    fun onChangeComment(newComment: String) = saveCommentState(newComment)

    fun onChangeSum(sumRecord: String) = saveSumRecordState(sumRecord)

    fun onChangeTemplateName(newName: String) = saveTemplateNameState(newName)

    fun onChangeImportantSwitch(isChecked: Boolean) {
        state.value = state.value?.copy(
            isImportant = isChecked
        )
    }

    fun onChangeTemplateSwitch(isChecked: Boolean) {
        state.value = state.value?.copy(
            isTemplate = isChecked
        )
        saveTemplateNameState(state.value?.templateName ?: "")
    }

    fun onCostsSelected() {
        val categories = state.value?.costsCategories
        state.value = state.value?.copy(
            recordType = RecordType.COSTS,
            selectedCategory = if (categories?.isNotEmpty() == true) categories[0].name else ""
        )
        println("onCostsSelected ${if (categories?.isNotEmpty() == true) categories[0].name else ""}")
    }

    fun onProfitSelected() {
        val categories = state.value?.profitCategories
        state.value = state.value?.copy(
            recordType = RecordType.PROFITS,
            selectedCategory = if (categories?.isNotEmpty() == true) categories[0].name else ""
        )
        println("onProfitSelected ${if (categories?.isNotEmpty() == true) categories[0].name else ""}")
    }

    fun onCashSelected() {
        state.value = state.value?.copy(
            moneyType = MoneyType.CASH
        )
    }

    fun onCardsSelected() {
        state.value = state.value?.copy(
            moneyType = MoneyType.CARDS
        )
    }

    fun onApplyTemplate(templatePosition: Int) {
        state.value = state.value?.copy(
            selectedTemplatePosition = templatePosition
        )
        val currentTemplate = state.value?.templates?.get(templatePosition - 1)
        if (currentTemplate != null) {
            val record = runBlocking {
                    recordRepository.getRecordById(currentTemplate.recordId).first()
                }
            if (record.recordType == RecordType.COSTS)
                onCostsSelected()
            else
                onProfitSelected()
            state.value = state.value?.copy(
                sumRecord = record.sumOfMoney.toString(),
                recordType = record.recordType,
                moneyType = record.moneyType,
                selectedCategory = record.category,
                canSave = true
            )
        }

    }

    fun onRevertToDefault() {
        state.value = state.value?.copy(
            selectedTemplatePosition = 0,
            sumRecord = "",
            recordType = RecordType.COSTS,
            moneyType = MoneyType.CASH,
            selectedCategory = state.value?.costsCategories?.get(0)?.name ?: "",
            isImportant = false,
            isTemplate = false,
            templateName = "",
            isValidTemplateName = false,
            canSave = false
        )
    }

    fun onSaveOrEditRecord(editingRecordId: Int, ifCompleted: () -> Unit) {

//        runBlocking {
//            categoryRepository.insert(Category(name="Тралик",type=CategoryType.CATEGORY_COSTS))
//            categoryRepository.insert(Category(name="Еда",type=CategoryType.CATEGORY_COSTS))
//            categoryRepository.insert(Category(name="Стипендия",type=CategoryType.CATEGORY_PROFIT))
//            categoryRepository.insert(Category(name="Зарплата",type=CategoryType.CATEGORY_PROFIT))
//        }
//        runBlocking { categoryRepository.deleteAll() }

        viewModelScope.launch {
            val sumMoney = Integer.parseInt(state.value?.sumRecord ?: "0")
            val recordType = state.value?.recordType ?: RecordType.COSTS
            val moneyType = state.value?.moneyType ?: MoneyType.CASH
            val categoryId: Int = withContext(Dispatchers.IO) {
                val categoryName = state.value?.selectedCategory ?: ""
                categoryRepository.getId(categoryName)
            }
            val category = state.value?.selectedCategory ?: ""
            val comment = state.value?.comment ?: ""

            if (editingRecordId == -1) {
                val newRecord = Record(
                    sumOfMoney = sumMoney,
                    categoryId = categoryId,
                    category = category,
                    recordType = recordType,
                    moneyType = moneyType,
                    isImportant = state.value?.isImportant ?: false,
                    comment = comment
                )
                val recordId = recordRepository.insert(newRecord).toInt()

                if (state.value?.isTemplate == true) {
                    val newTemplate = Template(
                        name = state.value?.templateName ?: "",
                        recordId = recordId
                    )
                    templateRepository.insert(newTemplate)
                }

            } else {
                recordRepository.update(
                    recordId = editingRecordId,
                    sumOfMoney = sumMoney,
                    recordType = recordType,
                    moneyType = moneyType,
                    categoryId = categoryId,
                    category = category,
                    comment = comment

                )
            }
            ifCompleted()
        }
    }

    companion object {
        private const val MAX_TEMPLATE_NAME_LENGTH = 15
        private const val MIN_TEMPLATE_NAME_LENGTH = 3
    }

}
