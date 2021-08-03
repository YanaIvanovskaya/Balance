package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.record.RecordType
import com.example.balance.data.template.Template
import com.example.balance.data.template.TemplateRepository
import com.example.balance.ui.recycler_view.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TemplateState(
    val currentChip: Int,
    val commonTemplates: MutableList<Item.TemplateItem>,
    val costsTemplates: MutableList<Item.TemplateItem>,
    val profitTemplates: MutableList<Item.TemplateItem>

) {

    companion object {
        fun default() = TemplateState(
            currentChip = 0,
            commonTemplates = mutableListOf(),
            costsTemplates = mutableListOf(),
            profitTemplates = mutableListOf()
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
                mapItems(newTemplateList)
                state.value = state.value?.copy(
                    commonTemplates = mapItems(newTemplateList),
                    costsTemplates = mapItems(newTemplateList, recordType = RecordType.COSTS),
                    profitTemplates = mapItems(newTemplateList, recordType = RecordType.PROFITS)
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
    ): MutableList<Item.TemplateItem> {
        val allTemplates: MutableList<Item.TemplateItem> = mutableListOf()

        items.reversed().forEach { template ->

            val record = withContext(Dispatchers.IO) {
                recordRepository.getRecordById(template.recordId).first()
            }
            val category = withContext(Dispatchers.IO) {
                categoryRepository.getNameById(record.categoryId).first()
            }

            val chipCondition = if (recordType != null) {
                record.recordType == recordType
            } else true

            if (chipCondition) {
                allTemplates.add(
                    Item.TemplateItem(
                        id = template.id,
                        name = template.name,
                        usage = template.frequencyOfUse,
                        sumMoney = record.sumOfMoney,
                        recordType = record.recordType,
                        moneyType = record.moneyType,
                        category = category
                    )
                )
            }
        }
        return allTemplates
    }

}