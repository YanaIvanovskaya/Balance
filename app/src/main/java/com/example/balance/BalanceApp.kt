package com.example.balance

import android.app.Application
import com.example.balance.data.RecordRepository
import com.example.balance.data.RecordRoomDatabase
import com.example.balance.data.UserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class BalanceApp : Application() {

    companion object {
        lateinit var dataStore: UserDataStore
        lateinit var applicationScope: CoroutineScope
        lateinit var database: RecordRoomDatabase
        lateinit var repository: RecordRepository
    }

    override fun onCreate() {
        super.onCreate()
        dataStore = UserDataStore(this)
        applicationScope = CoroutineScope(SupervisorJob())
        database = RecordRoomDatabase.getDatabase(this, applicationScope)
        repository = RecordRepository(database.RecordDao())
    }

}

