package com.example.balance.data.record

import com.example.balance.data.Category
import kotlinx.coroutines.flow.Flow


class RecordRepository(private val recordDao: RecordDao) {

    val allRecords: Flow<List<Record>> = recordDao.getAllRecords()

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

    fun getSum(recordType: RecordType, moneyType: MoneyType): Flow<Int?> =
        recordDao.getSum(recordType, moneyType)

    suspend fun deleteRecordById(recordId:Int) = recordDao.deleteRecordById(recordId)

    suspend fun setImportance(recordId: Int,isImportant: Boolean) = recordDao.setImportance(recordId,isImportant)


}