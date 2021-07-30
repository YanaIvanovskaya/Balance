package com.example.balance.data.category

import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    val allCostsCategory: Flow<List<Category>> = categoryDao.getAll(CategoryType.CATEGORY_COSTS)

    val allProfitCategory: Flow<List<Category>> = categoryDao.getAll(CategoryType.CATEGORY_PROFIT)

    suspend fun insert(category: Category) = categoryDao.insert(category)

    fun getCategoryNames(categoryType: CategoryType): List<String> = categoryDao.getCategoryNames(categoryType)

    suspend fun deleteAll() = categoryDao.deleteAll()

    fun getId(name: String) = categoryDao.getId(name)

    fun getNameById(id: Int) = categoryDao.getNameById(id)

}