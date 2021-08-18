package com.example.balance.data.record

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecordRepository(private val recordDao: RecordDao) {

    val allRecords: Flow<List<Record>> = recordDao.getAllRecords()

    fun getYearsOfUse(): Flow<List<Int>> = recordDao.getYears()

    fun getRecordById(recordId: Int): Flow<Record> = recordDao.getRecordById(recordId)

    fun getSum(recordType: RecordType, moneyType: MoneyType): Flow<Int> =
        recordDao.getSum(recordType, moneyType)
            .map { it ?: 0 }

    fun getSum(recordType: RecordType): Flow<Int> = recordDao.getSum(recordType).map { it ?: 0 }

    fun getCommonSum(): Flow<Int> = recordDao.getCommonSum().map { it ?: 0 }

    fun getMonthlyAmount(recordType: RecordType, month: Int, year: Int): Flow<Int?> =
        recordDao.getMonthlyAmount(recordType, month, year).map { it ?: 0 }

    fun getMonthlyAmount(categoryId: Int, month: Int, year: Int): Flow<Int?> =
        recordDao.getMonthlyAmount(categoryId, month, year).map { it ?: 0 }

    fun getMonthsInYear(year: Int): Flow<List<Int>> = recordDao.getMonthsInYear(year)

    fun getRecordsByType(recordType: RecordType): Flow<List<Record>> =
        recordDao.getRecordsByType(recordType)

    fun getSumByCategoryId(categoryId: Int): Flow<Int> =
        recordDao.getSumByCategoryId(categoryId).map { it ?: 0 }

    suspend fun update(
        recordId: Int,
        sumOfMoney: Int,
        recordType: RecordType,
        moneyType: MoneyType,
        categoryId: Int,
        comment: String
    ) = recordDao.update(
        recordId = recordId,
        sumOfMoney = sumOfMoney,
        recordType = recordType,
        moneyType = moneyType,
        categoryId = categoryId,
        comment = comment
    )

    suspend fun insert(record: Record) = recordDao.insert(record)

    suspend fun deleteRecordById(recordId: Int) = recordDao.deleteRecordById(recordId)

    suspend fun deleteAll() = recordDao.deleteAll()

    suspend fun setImportance(recordId: Int, isImportant: Boolean) =
        recordDao.setImportance(recordId, isImportant)

}