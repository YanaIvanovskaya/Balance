package com.example.balance.data.record

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber


class RecordRepository(private val recordDao: RecordDao) {

    val allRecords: Flow<List<Record>> = recordDao.getAllRecords()

    fun getYearsOfUse(): Flow<List<Int>> = recordDao.getYears()

    suspend fun insert(record: Record) = recordDao.insert(record)

    fun getRecordById(recordId: Int): Flow<Record> = recordDao.getRecordById(recordId)

    fun getLastRecord(): Flow<Record> = recordDao.getLastRecord()

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

    fun getSum(recordType: RecordType, moneyType: MoneyType): Flow<Int> =
        recordDao.getSum(recordType, moneyType)
            .map { it ?: 0 }

    fun getSumByMoneyType(moneyType: MoneyType): Flow<Int> =
        recordDao.getSumByMoneyType(moneyType).map { it ?: 0 }

    fun getCommonSum(): Flow<Int> = recordDao.getCommonSum().map { it ?: 0 }

    fun getMonthlyAmount(recordType: RecordType, month: Int, year: Int): Flow<Int?> =
        recordDao.getMonthlyAmount(recordType, month, year).map { it ?: 0 }

    suspend fun deleteRecordById(recordId: Int) = recordDao.deleteRecordById(recordId)

    suspend fun deleteAll() = recordDao.deleteAll()

    suspend fun setImportance(recordId: Int, isImportant: Boolean) =
        recordDao.setImportance(recordId, isImportant)

    fun getMonthsInYear(year: Int): Flow<List<Int>> = recordDao.getMonthsInYear(year)
}