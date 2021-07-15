package com.example.balance

import android.app.Application
import com.example.balance.data.UserDataStore

class BalanceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        dataStore = UserDataStore(this)
    }

    companion object {
        lateinit var dataStore: UserDataStore
    }

}
