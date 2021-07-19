package com.example.balance.data

import kotlinx.coroutines.flow.Flow


class RecordRepository(private val recordDao: RecordDao) {

    val allRecords: Flow<List<Record>> = recordDao.getAllRecords()

    suspend fun insert(record: Record) = recordDao.insert(record)

}