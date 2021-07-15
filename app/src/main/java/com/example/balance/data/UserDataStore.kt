package com.example.balance.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_preferences")

class UserDataStore(
    context: Context
) {
    private var mDataStore = context.dataStore

    val passcode: Flow<String?>
        get() = mDataStore.data.map { preferences ->
            preferences[KEY_PASSCODE] ?: ""
        }

    suspend fun savePasscode(newPasscode: String) {
        println("SAVING...")
        println("PASSCODE OLD _>${passcode}")
        passcode.collect { value -> println("VALUE${value}") }

        mDataStore.edit { preferences ->
            preferences[KEY_PASSCODE] = newPasscode
        }

        println("PASSCODE NEW _>${passcode}")
    }

    companion object {
        val KEY_PASSCODE = stringPreferencesKey("key_passcode")
    }

}