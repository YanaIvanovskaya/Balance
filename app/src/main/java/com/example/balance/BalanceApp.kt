package com.example.balance

import android.app.Application
import com.example.balance.data.*
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class BalanceApp : Application() {

    companion object {
        lateinit var dataStore: UserDataStore
        lateinit var applicationScope: CoroutineScope
        lateinit var database: RecordRoomDatabase
        lateinit var recordRepository: RecordRepository
        lateinit var categoryRepository: CategoryRepository
        lateinit var templateRepository: TemplateRepository
    }

    override fun onCreate() {
        super.onCreate()
        dataStore = UserDataStore(this)
        applicationScope = CoroutineScope(SupervisorJob())
        database = RecordRoomDatabase.getDatabase(this, applicationScope)
        recordRepository = RecordRepository(database.RecordDao())
        categoryRepository = CategoryRepository(database.CategoryDao())
        templateRepository = TemplateRepository(database.TemplateDao())
        AndroidThreeTen.init(this)
    }

}

