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
    val commonTemplates: List<Item>,
    val costsTemplates: List<Item>,
    val profitTemplates: List<Item>

) {

    companion object {
        fun default() = TemplateState(
            currentChip = 0,
            commonTemplates = listOf(),
            costsTemplates = listOf(),
            profitTemplates = listOf()
        )
    }

}

class TemplatesViewModel(
    val recordRepository: RecordRepository,
    val templateRepository: TemplateRepository,
    val categoryRepository: CategoryRepository
) : ViewModel() {

    val state = MutableLiveData(TemplateState.default())

    val allTemplates = MutableLiveData<List<Item.TemplateItem>>(listOf())

    init {
        templateRepository.allTemplates
            .map { newTemplateList -> mapItems(newTemplateList)
//                state.value = state.value?.copy(
//                    commonTemplates = mapItems(newTemplateList.reversed())
//                            costsTemplates =
//                )
            }
            .onEach(allTemplates::setValue)
            .launchIn(viewModelScope)
    }


    private suspend fun mapItems(items: List<Template>): MutableList<Item.TemplateItem> {
        val allTemplates: MutableList<Item.TemplateItem> = mutableListOf()

        items.forEach { template ->

            val record = withContext(Dispatchers.IO) {
                recordRepository.getRecordById(template.recordId).first()
            }
            val category = withContext(Dispatchers.IO) {
                categoryRepository.getNameById(record.categoryId).first()
            }

            val chipCondition = when (state.value?.currentChip) {
                1 -> record.recordType == RecordType.PROFITS
                2 -> record.recordType == RecordType.COSTS
                else -> true
            }

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