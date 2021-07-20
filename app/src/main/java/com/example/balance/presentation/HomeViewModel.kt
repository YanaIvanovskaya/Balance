package com.example.balance.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.balance.data.Record
import com.example.balance.data.RecordRepository
import com.example.balance.data.UserDataStore

class HomeViewModel(
    private val recordRepository: RecordRepository,
    private val datastore: UserDataStore
) : ViewModel() {

    val allRecords: LiveData<List<Record>> = recordRepository.allRecords.asLiveData()

    val sumCash = datastore.sumCash.asLiveData()

    val sumCards = datastore.sumCash.asLiveData()

}
