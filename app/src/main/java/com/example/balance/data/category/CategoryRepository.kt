package com.example.balance.data.category

import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    val allCategoriesWithDeleted: Flow<List<Category>> = categoryDao.getAllCategoriesWithDeleted()

    val allCostsCategory: Flow<List<Category>> =
        categoryDao.getCategoriesByType(CategoryType.CATEGORY_COSTS)

    val allProfitCategory: Flow<List<Category>> =
        categoryDao.getCategoriesByType(CategoryType.CATEGORY_PROFIT)

    suspend fun insert(category: Category) = categoryDao.insert(category)

    suspend fun insert(categories: List<Category>) = categoryDao.insert(categories)

    suspend fun update(id: Int, name: String) = categoryDao.update(id, name)

    suspend fun deleteCategoryById(id: Int) = categoryDao.deleteCategoryById(id)

    fun getId(name: String) = categoryDao.getId(name)

    fun getNameById(id: Int) = categoryDao.getNameById(id)

}