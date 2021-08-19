package com.example.balance.presentation.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.Template
import com.example.balance.data.template.TemplateRepository
import com.example.balance.ui.recycler_view.item.Item
import com.example.balance.ui.recycler_view.item.NoItemsItem
import com.example.balance.ui.recycler_view.item.TemplateItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TemplateState(
    val currentChip: Int,
    val commonTemplates: MutableList<Item>,
    val costsTemplates: MutableList<Item>,
    val profitTemplates: MutableList<Item>,
    val isContentLoaded: Boolean

) {

    companion object {
        fun default() = TemplateState(
            currentChip = 0,
            commonTemplates = mutableListOf(),
            costsTemplates = mutableListOf(),
            profitTemplates = mutableListOf(),
            isContentLoaded = false
        )
    }

}

class TemplatesViewModel(
    val recordRepository: RecordRepository,
    val templateRepository: TemplateRepository,
    val categoryRepository: CategoryRepository
) : ViewModel() {

    val state = MutableLiveData(TemplateState.default())

    init {
        templateRepository.allTemplates
            .onEach { newTemplateList ->
                state.value = state.value?.copy(
                    commonTemplates = mapItems(newTemplateList),
                    costsTemplates = mapItems(
                        newTemplateList,
                        recordType = RecordType.COSTS
                    ),
                    profitTemplates = mapItems(
                        newTemplateList,
                        recordType = RecordType.PROFITS
                    )
                )
                state.value = state.value?.copy(
                    isContentLoaded = true
                )
            }
            .launchIn(viewModelScope)
    }

    fun saveCurrentChip(chipNumber: Int) {
        state.value = state.value?.copy(
            currentChip = chipNumber
        )
    }

    fun removeTemplate(templateId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            templateRepository.deleteTemplateById(templateId)
        }
    }

    private suspend fun mapItems(
        items: List<Template>,
        recordType: RecordType? = null
    ): MutableList<Item> {
        val allTemplates: MutableList<Item> = mutableListOf()
        items.reversed().forEach { template ->
            val record = withContext(Dispatchers.IO) {
                recordRepository.getRecordById(template.recordId).first()
            }
            val category = withContext(Dispatchers.IO) {
                categoryRepository.getNameById(record.categoryId).first()
            }

            val chipCondition =
                if (recordType != null) record.recordType == recordType
                else true
            if (chipCondition) {
                allTemplates.add(
                    TemplateItem(
                        id = template.id,
                        name = template.name,
                        sumMoney = record.sumOfMoney,
                        recordType = record.recordType,
                        moneyType = record.moneyType,
                        category = category
                    )
                )
            }
        }
//        if (allTemplates.isEmpty()) {
//            allTemplates.add(
//                when (recordType) {
//                    RecordType.COSTS -> NoItemsItem(
//                        message = "Здесь будут шаблоны по расходам",
//                        enableAdd = false
//                    )
//                    RecordType.PROFITS -> NoItemsItem(
//                        message = "здесь будут шаблоны по доходам",
//                        enableAdd = false
//                    )
//                    null -> NoItemsItem(
//                        message = "Здесь будут все ваши шаблоны",
//                        enableAdd = false
//                    )
//                }
//            )
//        }
        return allTemplates
    }

}