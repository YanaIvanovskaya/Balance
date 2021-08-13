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
import com.example.balance.ui.recycler_view.item.NoItemsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


data class CategoryState(
    val currentChip: Int,
    val commonCategories: MutableList<Item>,
    val costsCategories: MutableList<Item>,
    val profitCategories: MutableList<Item>
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
        return state.value?.profitCategories?.filterIsInstance<CategoryItem>()?.map {
            it.name
        } ?: listOf()
    }

    fun getCostsCategoryNames(): List<String> {
        return state.value?.costsCategories?.filterIsInstance<CategoryItem>()?.map {
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
        }
    }

    private fun mapItems(
        items: List<Category>,
        categoryType: CategoryType? = null
    ): MutableList<Item> {
        val allCategories: MutableList<Item> = mutableListOf()

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
        if (allCategories.isEmpty()) {
            allCategories.add(
                when (categoryType) {
                    CategoryType.CATEGORY_COSTS -> NoItemsItem(message = "Здесь будут категории расходов",enableAdd = false)
                    CategoryType.CATEGORY_PROFIT -> NoItemsItem(message = "Здесь будут категории доходов",enableAdd = false)
                    null -> NoItemsItem(message = "Здесь будут все ваши категории",enableAdd = false)
                }
            )
        }
        return allCategories
    }

}