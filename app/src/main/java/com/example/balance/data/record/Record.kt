package com.example.balance.data.record

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

enum class RecordType {
    COSTS,
    PROFITS
}

enum class MoneyType {
    CASH,
    CARDS
}

@Entity(tableName = "record_table")
data class Record(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var time: String = LocalTime.now().toString(),
    var day: Int = currentDate.dayOfMonth,
    var month: Int = currentDate.month.value,
    var year: Int = currentDate.year,
    var weekDay: Int = currentDate.dayOfWeek.value,
    var isVisible: Boolean = true,
    var isImportant: Boolean = false,
    var sumOfMoney: Int,
    var recordType: RecordType,
    var moneyType: MoneyType,
    @ColumnInfo(name = "category_id")
    var categoryId: Int,
    var comment: String
) {

    companion object {
        private var currentDate: LocalDate = LocalDate.now()
    }

}

@Dao
interface RecordDao {

    @Query("SELECT * FROM record_table")
    fun getAllRecords(): Flow<List<Record>>

    @Query("SELECT * FROM record_table WHERE id = :recordId")
    fun getRecordById(recordId: Int): Flow<Record>

    @Query("SELECT SUM(sumOfMoney) FROM record_table WHERE recordType =:recordType AND moneyType =:moneyType")
    fun getSum(recordType: RecordType, moneyType: MoneyType): Flow<Int?>

    @Query("SELECT SUM(sumOfMoney) FROM record_table WHERE recordType =:recordType")
    fun getSum(recordType: RecordType): Flow<Int?>

    @Query("SELECT SUM(sumOfMoney) FROM record_table WHERE moneyType =:moneyType")
    fun getSumByMoneyType(moneyType: MoneyType): Flow<Int?>

    @Query("SELECT SUM(sumOfMoney) FROM record_table")
    fun getCommonSum(): Flow<Int?>

    @Query("UPDATE record_table SET sumOfMoney=:sumOfMoney,recordType= :recordType,moneyType=:moneyType,category_id=:categoryId,comment=:comment WHERE id = :recordId")
    suspend fun update(
        recordId: Int,
        sumOfMoney: Int,
        recordType: RecordType,
        moneyType: MoneyType,
        categoryId: Int,
        comment: String
    )

    @Query("UPDATE record_table SET isImportant=:isImportant WHERE id = :recordId")
    suspend fun setImportance(recordId: Int, isImportant: Boolean)

    @Query("DELETE FROM record_table")
    suspend fun deleteAll()

    @Query("DELETE FROM record_table WHERE id=:recordId")
    suspend fun deleteRecordById(recordId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: Record): Long


    // statistics
    @Query("SELECT DISTINCT month FROM record_table WHERE year=:year")
    fun getMonthsInYear(year: Int): Flow<List<Int>>

    @Query("SELECT SUM(sumOfMoney) FROM record_table WHERE recordType =:recordType AND month =:month AND year=:year")
    fun getMonthlyAmount(recordType: RecordType, month: Int, year: Int): Flow<Int?>

    @Query("SELECT SUM(sumOfMoney) FROM record_table WHERE category_id =:categoryId AND month =:month AND year=:year")
    fun getMonthlyAmount(categoryId: Int, month: Int, year: Int): Flow<Int?>

    @Query("SELECT DISTINCT year FROM record_table")
    fun getYears(): Flow<List<Int>>

    @Query("SELECT * FROM record_table WHERE recordType=:recordType")
    fun getRecordsByType(recordType: RecordType): Flow<List<Record>>

    @Query("SELECT SUM(sumOfMoney) FROM record_table WHERE category_id =:categoryId")
    fun getSumByCategoryId(categoryId: Int): Flow<Int?>

}


