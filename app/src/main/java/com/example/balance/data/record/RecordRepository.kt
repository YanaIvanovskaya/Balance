package com.example.balance.data.record

import kotlinx.coroutines.flow.Flow


class RecordRepository(private val recordDao: RecordDao) {

    val allRecords: Flow<List<Record>> = recordDao.getAllRecords()

    suspend fun insert(record: Record) = recordDao.insert(record)

    fun getRecordById(recordId: Int): Flow<Record> = recordDao.getRecordById(recordId)

}