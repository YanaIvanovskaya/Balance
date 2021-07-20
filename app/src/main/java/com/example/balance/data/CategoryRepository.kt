package com.example.balance.data

import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    val allCostsCategory: Flow<List<Category>> = categoryDao.getAll(CategoryType.CATEGORY_COSTS)

    val allProfitCategory: Flow<List<Category>> = categoryDao.getAll(CategoryType.CATEGORY_PROFIT)

    suspend fun insert(category: Category) = categoryDao.insert(category)

    fun getId(name: String) = categoryDao.getId(name)

    fun getName(id: Int) = categoryDao.getNameById(id)

}