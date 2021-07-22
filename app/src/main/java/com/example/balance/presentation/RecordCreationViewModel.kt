package com.example.balance.presentation

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.balance.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

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

    init {
        updateTemplates()
        updateCostsCategory()
//        println(state.value?.selectedCategory)
    }

    val templates = templateRepository.allTemplates.asLiveData()

    val costsCategories = categoryRepository.allCostsCategory.asLiveData()

    val profitCategories = categoryRepository.allProfitCategory.asLiveData()

    fun saveSumRecordState(newSumRecord: String) {
        state.value = state.value?.copy(
            sumRecord = newSumRecord,
            canSave = newSumRecord.isNotEmpty()
        )
    }

    fun onChangeComment(newComment: String) = saveCommentState(newComment)

    private fun saveCommentState(newComment: String) {
        state.value = state.value?.copy(
            comment = newComment
        )
    }

    fun updateTemplates() {
        viewModelScope.launch {
            state.value = state.value?.copy(
                templates = templateRepository.allTemplates.first()
            )
        }
    }

    fun onChangeTemplate(templatePosition: Int) {
        state.value = state.value?.copy(
            selectedTemplatePosition = templatePosition
        )
    }

    fun updateCostsCategory() {
        viewModelScope.launch {
            val listCostsCategories = categoryRepository.allCostsCategory.first()
            if (listCostsCategories.isNotEmpty() && state.value?.recordType == RecordType.COSTS) {
                state.value = state.value?.copy(
                    categories = listCostsCategories,
                    selectedCategory = listCostsCategories[0].name
                )
            }
        }
    }

    fun updateProfitCategory() {
        viewModelScope.launch {
            val listProfitCategories = categoryRepository.allProfitCategory.first()
            if (listProfitCategories.isNotEmpty() && state.value?.recordType == RecordType.PROFITS) {
                state.value = state.value?.copy(
                    categories = listProfitCategories,
                    selectedCategory = listProfitCategories[0].name
                )
            }
        }
    }


    fun onChangeSum(sumRecord: String) = saveSumRecordState(sumRecord)

    private fun getCategories(): Array<String> =
        state.value?.categories?.map { category -> category.name }?.toTypedArray()
            ?: arrayOf("Ничего нет(")

    fun getTemplates(): Array<String> =
        state.value?.templates?.map { template -> template.name }?.toTypedArray()
            ?: arrayOf("Ничего нет(")

    fun showCategoryDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Выберите категорию")
        val categories = getCategories()
        val checkedItem = 0
        builder.setSingleChoiceItems(categories, checkedItem) { dialog, which ->
            onCategorySelected(categories[which])
            dialog.cancel()
        }
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
    }


    fun onCostsSelected() {
        state.value = state.value?.copy(
            recordType = RecordType.COSTS
        )
        updateCostsCategory()
        println("onCostsSelected")
    }

    fun onProfitSelected() {
        state.value = state.value?.copy(
            recordType = RecordType.PROFITS
        )
        updateProfitCategory()
    }

    fun onCategorySelected(category: String) {
        state.value = state.value?.copy(
            selectedCategory = category
        )
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

    fun onChangeImportantSwitch(isChecked: Boolean) {
        state.value = state.value?.copy(
            isImportant = isChecked
        )
    }

    fun onChangeTemplateSwitch(isChecked: Boolean) {
        state.value = state.value?.copy(
            isTemplate = isChecked
        )
    }

    fun onSaveRecord() {

//        runBlocking {
//            categoryRepository.insert(Category(name="Тралик",type=CategoryType.CATEGORY_COSTS))
//            categoryRepository.insert(Category(name="Еда",type=CategoryType.CATEGORY_COSTS))
//            categoryRepository.insert(Category(name="Стипендия",type=CategoryType.CATEGORY_PROFIT))
//            categoryRepository.insert(Category(name="Зарплата",type=CategoryType.CATEGORY_PROFIT))
//        }

        runBlocking {
            val sumMoney = Integer.parseInt(state.value?.sumRecord ?: "0")
            val recordType = state.value?.recordType ?: RecordType.COSTS
            val factor = if (recordType == RecordType.COSTS) -1 else 1
            val moneyType = state.value?.moneyType ?: MoneyType.CASH
            val categoryId = withContext(Dispatchers.IO) {
                categoryRepository.getId(
                    state.value?.selectedCategory ?: ""
                ).first()
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
            recordRepository.insert(newRecord)
        }
    }
}
