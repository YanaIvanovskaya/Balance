package com.example.balance.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.balance.data.Record
import com.example.balance.data.RecordRepository
import kotlinx.coroutines.launch

class RecordViewModel(
    private val repository: RecordRepository
) : ViewModel() {

    val allRecords: LiveData<List<Record>> = repository.allRecords.asLiveData()

}