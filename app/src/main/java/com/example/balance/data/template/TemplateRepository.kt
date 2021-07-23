package com.example.balance.data.template

import com.example.balance.data.Template
import com.example.balance.data.TemplateDao
import kotlinx.coroutines.flow.Flow

class TemplateRepository(private val templateDao: TemplateDao) {

    val allTemplates: Flow<List<Template>> = templateDao.getAll()

    suspend fun insert(template: Template) = templateDao.insert(template)

}