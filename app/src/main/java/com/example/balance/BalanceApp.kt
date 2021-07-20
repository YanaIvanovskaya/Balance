package com.example.balance

import android.app.Application
import com.example.balance.data.CategoryRepository
import com.example.balance.data.RecordRepository
import com.example.balance.data.RecordRoomDatabase
import com.example.balance.data.UserDataStore
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
    }

    override fun onCreate() {
        super.onCreate()
        dataStore = UserDataStore(this)
        applicationScope = CoroutineScope(SupervisorJob())
        database = RecordRoomDatabase.getDatabase(this, applicationScope)
        recordRepository = RecordRepository(database.RecordDao())
        categoryRepository = CategoryRepository(database.CategoryDao())
        AndroidThreeTen.init(this)
    }

}

