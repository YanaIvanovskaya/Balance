package com.example.balance.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class BalanceRepository(
    private val datastore: UserDataStore
) {

    var sumCash: Int = 0
        private set
    var sumCards: Int = 0
        private set
    var sumBalanceCash: Int = 0
        private set
    var sumBalanceCards: Int = 0
        private set

    suspend fun loadBalance() {
        sumCash = withContext(Dispatchers.IO) { datastore.sumCash.first() }
        sumCards = withContext(Dispatchers.IO) { datastore.sumCards.first() }
        sumBalanceCash = withContext(Dispatchers.IO) { datastore.balanceCash.first() }
        sumBalanceCards = withContext(Dispatchers.IO) { datastore.balanceCards.first() }
    }

    suspend fun saveBalance(cash: Int, cards: Int) {
        withContext(Dispatchers.IO) {
            datastore.saveBalanceCash(cash)
            datastore.saveBalanceCards(cards)
        }
    }

}
