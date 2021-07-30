package com.example.balance.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class BalanceRepository(
    val datastore: UserDataStore
) {

    var sumCash: Int = 0
        private set
    var sumCards: Int = 0
        private set

    suspend fun loadBalance() {
        sumCash = withContext(Dispatchers.IO) { datastore.sumCash.first() }
        sumCards = withContext(Dispatchers.IO) { datastore.sumCards.first() }
    }

}
