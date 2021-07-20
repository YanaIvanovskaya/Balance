package com.example.balance.presentation

import androidx.datastore.dataStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class RecordCreationState(
    val sumRecord: String,
    val recordType: RecordType,
    val moneyType: MoneyType,
    val canSave: Boolean,
    val categories: List<Category>,
    val selectedCategory: String,
    val comment: String
) {

    companion object {
        fun default() = RecordCreationState(
            sumRecord = "",
            recordType = RecordType.COSTS,
            moneyType = MoneyType.CASH,
            selectedCategory = "",
            canSave = false,
            categories = listOf(),
            comment = "comment"
        )
    }

}

class RecordCreationViewModel(
    private val recordRepository: RecordRepository,
    private val categoryRepository: CategoryRepository,
    private val dataStore: UserDataStore
) : ViewModel() {

    val state = MutableLiveData(RecordCreationState.default())

    private fun saveSumRecordState(newSumRecord: String) {
        state.value = state.value?.copy(
            sumRecord = newSumRecord,
            canSave = newSumRecord.isNotEmpty()
        )
    }

    fun onChangeSum(sumRecord: String) = saveSumRecordState(sumRecord)

    fun getCategories(): Array<String> =
        state.value?.categories?.map { category -> category.name }?.toTypedArray()
            ?: arrayOf("Ничего нет(")

    fun onCostsSelected() {
        viewModelScope.launch {
            val listCostsCategories = categoryRepository.allCostsCategory.first()

            state.value = state.value?.copy(
                recordType = RecordType.COSTS,
                categories = listCostsCategories,
                selectedCategory = listCostsCategories[0].name
            )
        }
    }

    fun onProfitSelected() {
        viewModelScope.launch {
            val listProfitCategories = categoryRepository.allProfitCategory.first()

            state.value = state.value?.copy(
                recordType = RecordType.PROFITS,
                categories = listProfitCategories,
                selectedCategory = listProfitCategories[0].name
            )
        }
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

    fun onSaveRecord() {
        viewModelScope.launch {
            val sumMoney = Integer.parseInt(state.value?.sumRecord ?: "0")

            val newRecord = Record(
                sumOfMoney = sumMoney,
                categoryId = categoryRepository.getId(state.value?.selectedCategory ?: "").first(),
                category = state.value?.selectedCategory ?: "",
                recordType = state.value?.recordType ?: RecordType.COSTS,
                moneyType = state.value?.moneyType ?: MoneyType.CASH,
                comment = state.value?.comment ?: ""
            )

            val factor = if (newRecord.recordType == RecordType.COSTS) -1 else 1

            when (newRecord.moneyType) {
                MoneyType.CASH -> dataStore.addSumCash(sumMoney * factor)
                MoneyType.CARDS -> dataStore.addSumCards(sumMoney * factor)
            }
            recordRepository.insert(newRecord)
        }
    }

}

//                categoryRepository.insert(Category(name = "Проезд", type = CategoryType.CATEGORY_COSTS))
//            categoryRepository.insert(Category(name = "Стипендия", type = CategoryType.CATEGORY_PROFIT))