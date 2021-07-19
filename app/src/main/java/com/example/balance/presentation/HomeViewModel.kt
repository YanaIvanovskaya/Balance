package com.example.balance.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.balance.data.Record
import com.example.balance.data.RecordRepository

class HomeViewModel(
    repository: RecordRepository
) : ViewModel() {

    val allRecords: LiveData<List<Record>> = repository.allRecords.asLiveData()

}
