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
            .map {
                Timber.d("Map sum. $recordType $moneyType")
                Timber.d("Map sum: $it")
                it ?: 0
            }

    fun getMonthlyAmount(recordType: RecordType, monthName: String, year: Int): Flow<Int?> =
        recordDao.getMonthlyAmount(recordType, monthName, year)

    suspend fun deleteRecordById(recordId: Int) = recordDao.deleteRecordById(recordId)

    suspend fun setImportance(recordId: Int, isImportant: Boolean) =
        recordDao.setImportance(recordId, isImportant)


}