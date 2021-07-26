package com.example.balance.data.template

import kotlinx.coroutines.flow.Flow

class TemplateRepository(private val templateDao: TemplateDao) {

    val allTemplates: Flow<List<Template>> = templateDao.getAll()

    suspend fun insert(template: Template) = templateDao.insert(template)

}