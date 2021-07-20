package com.example.balance.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
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
        mDataStore.edit { preferences ->
            preferences[KEY_PASSCODE] = newPasscode
        }
    }

    val sumCash: Flow<Int>
        get() = mDataStore.data.map { preferences ->
            preferences[KEY_CASH] ?: 0
        }

    suspend fun addSumCash(cash: Int) {
        println("addSumCash $sumCash")
        mDataStore.edit { preferences ->
            preferences[KEY_CASH] = preferences[KEY_CASH]?.plus(cash) ?: 0
        }

    }

    val sumCards: Flow<Int>
        get() = mDataStore.data.map { preferences ->
            preferences[KEY_CARDS] ?: 0
        }

    suspend fun addSumCards(cards: Int) {
        mDataStore.edit { preferences ->
            preferences[KEY_CARDS] = preferences[KEY_CARDS]?.plus(cards) ?: 0
        }
    }

    companion object {
        val KEY_PASSCODE = stringPreferencesKey("key_passcode")
        val KEY_CASH = intPreferencesKey("key_cash")
        val KEY_CARDS = intPreferencesKey("key_cards")
    }

}