package com.example.balance.presentation

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.balance.data.*
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.template.TemplateRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class RecordCreationState(
    val selectedTemplatePosition: Int,
    val templates: List<Template>,
    val sumRecord: String,
    val recordType: RecordType,
    val moneyType: MoneyType,
    val selectedCategory: String,
    val categories: List<Category>,
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
            sumRecord = "",
            recordType = RecordType.COSTS,
            moneyType = MoneyType.CASH,
            selectedCategory = "",
            categories = listOf(),
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
    private val dataStore: UserDataStore
) : ViewModel() {

    val state = MutableLiveData(RecordCreationState.default())

    var onCreateComplete: () -> Unit = {}

    val costsCategories = categoryRepository.allCostsCategory.asLiveData()

    val profitCategories = categoryRepository.allProfitCategory.asLiveData()

    init {
        onRevertToDefault()

        templateRepository.allTemplates
            .onEach { newTemplates: List<Template> ->
                // TODO: 23.07.2021 Modify state
                state.value = state.value?.copy(
                    templates = newTemplates
                )
            }
            .launchIn(viewModelScope)

    }

    private fun saveSumRecordState(newSumRecord: String) {
        val canSave = if (state.value?.isTemplate == true)
            state.value?.templateName?.isNotEmpty() == true
        else newSumRecord.isNotEmpty()
        state.value = state.value?.copy(
            sumRecord = newSumRecord,
            canSave = canSave
        )
    }

    private fun saveCommentState(newComment: String) {
        state.value = state.value?.copy(
            comment = newComment
        )
    }

    private fun saveTemplateNameState(newName: String) {
        val canSave =
            if (state.value?.isTemplate == true) newName.isNotEmpty()
            else state.value?.sumRecord?.isNotEmpty() == true
        if (newName.length <= MAX_TEMPLATE_NAME_LENGTH) {
            state.value = state.value?.copy(
                templateName = newName,
                isValidTemplateName = newName.isNotEmpty(),
                canSave = canSave
            )
        }
    }

    private fun getCategories(): Array<String> {
        return state.value?.categories?.map { category -> category.name }?.toTypedArray()
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

    fun updateCostsCategory() {
        val listCostsCategories = runBlocking {
            categoryRepository.allCostsCategory.first()
        }
        if (listCostsCategories.isNotEmpty() && state.value?.recordType == RecordType.COSTS) {
            state.value = state.value?.copy(
                categories = listCostsCategories,
                selectedCategory = listCostsCategories[0].name
            )
        }
    }

    fun updateProfitCategory() {
        val listProfitCategories = runBlocking {
            categoryRepository.allProfitCategory.first()
        }
        if (listProfitCategories.isNotEmpty() && state.value?.recordType == RecordType.PROFITS) {
            state.value = state.value?.copy(
                categories = listProfitCategories,
                selectedCategory = listProfitCategories[0].name
            )
        }
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
        state.value = state.value?.copy(
            recordType = RecordType.COSTS
        )
        updateCostsCategory()
    }

    fun onProfitSelected() {
        state.value = state.value?.copy(
            recordType = RecordType.PROFITS
        )
        updateProfitCategory()
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
            val record =
                runBlocking {
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
        )
        updateCostsCategory()
    }

    fun onSaveRecord() {

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
            val factor = if (recordType == RecordType.COSTS) -1 else 1
            val moneyType = state.value?.moneyType ?: MoneyType.CASH
            val categoryId: Int = withContext(Dispatchers.IO) {
                val categoryName = state.value?.selectedCategory ?: ""
                categoryRepository.getId(categoryName)
            }
            when (moneyType) {
                MoneyType.CASH -> dataStore.addSumCash(sumMoney * factor)
                MoneyType.CARDS -> dataStore.addSumCards(sumMoney * factor)
            }
            val newRecord = Record(
                sumOfMoney = sumMoney,
                categoryId = categoryId,
                category = state.value?.selectedCategory ?: "",
                recordType = recordType,
                moneyType = moneyType,
                isImportant = state.value?.isImportant ?: false,
                comment = state.value?.comment ?: ""
            )
            val recordId = recordRepository.insert(newRecord).toInt()

            if (state.value?.isTemplate == true) {
                val newTemplate = Template(
                    name = state.value?.templateName ?: "",
                    recordId = recordId
                )
                templateRepository.insert(newTemplate)
            }

            onCreateComplete()
        }
    }

    companion object {
        private const val MAX_TEMPLATE_NAME_LENGTH = 15
    }

}
