package com.example.balance

import android.app.Application
import com.example.balance.data.*
import com.example.balance.data.category.CategoryRepository
import com.example.balance.data.record.RecordRepository
import com.example.balance.data.template.TemplateRepository
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class BalanceApp : Application() {

    companion object {
        lateinit var dataStore: UserDataStore
        lateinit var applicationScope: CoroutineScope
        lateinit var database: BalanceDatabase
        lateinit var recordRepository: RecordRepository
        lateinit var categoryRepository: CategoryRepository
        lateinit var templateRepository: TemplateRepository
        lateinit var balanceRepository: BalanceRepository
    }

    override fun onCreate() {
        super.onCreate()
        dataStore = UserDataStore(this)
        applicationScope = CoroutineScope(SupervisorJob())
        database = BalanceDatabase.getDatabase(this, applicationScope)
        recordRepository = RecordRepository(database.RecordDao())
        categoryRepository = CategoryRepository(database.CategoryDao())
        templateRepository = TemplateRepository(database.TemplateDao())
        balanceRepository = BalanceRepository(dataStore)
        AndroidThreeTen.init(this)
        Timber.plant(Timber.DebugTree())
    }

}

