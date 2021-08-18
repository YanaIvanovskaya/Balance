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

    val sumCash: Flow<Int>
        get() = mDataStore.data.map { preferences ->
            preferences[KEY_CASH] ?: 0
        }

    val sumCards: Flow<Int>
        get() = mDataStore.data.map { preferences ->
            preferences[KEY_CARDS] ?: 0
        }

    val balanceCards: Flow<Int>
        get() = mDataStore.data.map { preferences ->
            preferences[KEY_BALANCE_CARDS] ?: 0
        }

    val balanceCash: Flow<Int>
        get() = mDataStore.data.map { preferences ->
            preferences[KEY_BALANCE_CASH] ?: 0
        }

    suspend fun savePasscode(newPasscode: String) {
        mDataStore.edit { preferences ->
            preferences[KEY_PASSCODE] = newPasscode
        }
    }

    suspend fun saveSumCash(cash: Int) {
        mDataStore.edit { preferences ->
            preferences[KEY_CASH] = cash
        }
    }

    suspend fun saveSumCards(cards: Int) {
        mDataStore.edit { preferences ->
            preferences[KEY_CARDS] = cards
        }
    }

    suspend fun saveBalanceCards(cards: Int) {
        mDataStore.edit { preferences ->
            preferences[KEY_BALANCE_CARDS] = cards
        }
    }

    suspend fun saveBalanceCash(cash: Int) {
        mDataStore.edit { preferences ->
            preferences[KEY_BALANCE_CASH] = cash
        }
    }


    companion object {
        val KEY_PASSCODE = stringPreferencesKey("key_passcode")
        val KEY_CASH = intPreferencesKey("key_cash")
        val KEY_CARDS = intPreferencesKey("key_cards")
        val KEY_BALANCE_CARDS = intPreferencesKey("key_balance_cards")
        val KEY_BALANCE_CASH = intPreferencesKey("key_balance_cash")
    }

}