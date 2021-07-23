package com.example.balance.data.category

import com.example.balance.data.Category
import com.example.balance.data.CategoryDao
import com.example.balance.data.CategoryType
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    val allCostsCategory: Flow<List<Category>> = categoryDao.getAll(CategoryType.CATEGORY_COSTS)

    val allProfitCategory: Flow<List<Category>> = categoryDao.getAll(CategoryType.CATEGORY_PROFIT)

    suspend fun insert(category: Category) = categoryDao.insert(category)

    suspend fun deleteAll() = categoryDao.deleteAll()

    fun getId(name: String) = categoryDao.getId(name)

}