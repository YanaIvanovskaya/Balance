package com.example.balance.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class RecordRepository(private val recordDao: RecordDao) {

    val allRecords: Flow<List<Record>> = recordDao.getAllRecords()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(record: Record) {
        recordDao.insert(record)
    }
}