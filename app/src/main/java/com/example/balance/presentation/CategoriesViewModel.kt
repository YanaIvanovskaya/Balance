package com.example.balance.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balance.Case
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.category.CategoryType
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.template.TemplateRepository
import com.example.balance.getMonthName
import com.example.balance.ui.recycler_view.item.CategoryItem
import com.example.balance.ui.recycler_view.item.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


data class CategoryState(
    val currentChip: Int,
    val commonCategories: MutableList<CategoryItem>,
    val costsCategories: MutableList<CategoryItem>,
    val profitCategories: MutableList<CategoryItem>
) {

    companion object {
        fun default() = CategoryState(
            currentChip = 0,
            commonCategories = mutableListOf(),
            costsCategories = mutableListOf(),
            profitCategories = mutableListOf()
        )
    }

}

class CategoriesViewModel(
    val recordRepository: RecordRepository,
    val templateRepository: TemplateRepository,
    val categoryRepository: CategoryRepository
) : ViewModel() {

    val state = MutableLiveData(CategoryState.default())

    init {
        categoryRepository.allCategories
            .onEach { newCategoryList ->
                mapItems(newCategoryList)
                state.value = state.value?.copy(
                    commonCategories = mapItems(newCategoryList),
                    costsCategories = mapItems(
                        newCategoryList,
                        categoryType = CategoryType.CATEGORY_COSTS
                    ),
                    profitCategories = mapItems(
                        newCategoryList,
                        categoryType = CategoryType.CATEGORY_PROFIT
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    fun saveCurrentChip(chipNumber: Int) {
        state.value = state.value?.copy(
            currentChip = chipNumber
        )
    }

    fun getProfitCategoryNames(): List<String> {
        return state.value?.profitCategories?.map {
            it.name
        } ?: listOf()
    }

    fun getCostsCategoryNames(): List<String> {
        return state.value?.costsCategories?.map {
            it.name
        } ?: listOf()
    }

    fun onSaveNewCategory(categoryName: String, categoryType: CategoryType) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.insert(
                Category(
                    name = categoryName,
                    type = categoryType,
                    isDeleted = false
                )
            )
        }
    }

    fun removeCategory(categoryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.deleteCategoryById(categoryId)
            val templatesWithLink = templateRepository.allTemplates.first().filter {
                recordRepository.getRecordById(it.recordId).first().categoryId == categoryId
            }

            if (templatesWithLink.isNotEmpty()) {
                templatesWithLink.forEach {
                    templateRepository.deleteTemplateById(it.id)
                }
            }
//
//            ) {
//                if (recordRepository.getRecordById(template.recordId)
//                        .first().categoryId == categoryId
//                ) {
//                    templateRepository.deleteTemplateById(template.id)
//                }
//            }
        }
    }

    private suspend fun mapItems(
        items: List<Category>,
        categoryType: CategoryType? = null
    ): MutableList<CategoryItem> {
        val allCategories: MutableList<CategoryItem> = mutableListOf()

        items.reversed().forEach { category ->

            val chipCondition = if (categoryType != null) {
                category.type == categoryType
            } else true

            if (chipCondition) {
                allCategories.add(
                    CategoryItem(
                        id = category.id,
                        name = category.name,
                        categoryType = category.type,
                        dateCreation = "${category.day} ${
                            getMonthName(
                                category.month ?: 0,
                                Case.OF
                            )
                        } ${category.year}"
                    )
                )
            }
        }
        return allCategories
    }

}