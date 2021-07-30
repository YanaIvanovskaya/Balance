package com.example.balance.data.record

import androidx.room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.format.TextStyle
import java.util.*

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
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var day: Int = date.dayOfMonth,
    var month: String = months[date.month.value] ?: "",
    var year: Int = date.year,
    var weekDay: String = weekDays[date.dayOfWeek.value] ?: "",

    var isVisible: Boolean = true,
    var isImportant: Boolean = false,
    val dateText: String = "$weekDay $day $month $year",

    var sumOfMoney: Int,
    var recordType: RecordType,
    var moneyType: MoneyType,
    @ColumnInfo(name = "category_id")
    var categoryId: Int,
    var comment: String
) {

    companion object {
        private var date: LocalDate = LocalDate.now()
        val months = mapOf(
            1 to "января",
            2 to "февраля",
            3 to "марта",
            4 to "апреля",
            5 to "мая",
            6 to "июня",
            7 to "июля",
            8 to "августа",
            9 to "сентября",
            10 to "октября",
            11 to "ноября",
            12 to "декабря"
        )

        private val weekDays = mapOf(
            1 to "пн",
            2 to "вт",
            3 to "ср",
            4 to "чт",
            5 to "пт",
            6 to "сб",
            7 to "вс",
        )
    }

}

@Dao
interface RecordDao {

    @Query("SELECT * FROM record_table")
    fun getAllRecords(): Flow<List<Record>>

    @Query("SELECT * FROM record_table WHERE id = :recordId")
    fun getRecordById(recordId: Int): Flow<Record>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: Record): Long


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

    @Query("SELECT SUM(sumOfMoney) FROM record_table WHERE recordType =:recordType AND moneyType =:moneyType")
    fun getSum(recordType: RecordType, moneyType: MoneyType): Flow<Int?>

    @Query("SELECT SUM(sumOfMoney) FROM record_table WHERE recordType =:recordType AND month =:monthName AND year=:year")
    fun getMonthlyAmount(recordType: RecordType, monthName: String, year: Int): Flow<Int?>

    @Query("SELECT DISTINCT year FROM record_table")
    fun getYears(): Flow<List<Int>>

    @Query("DELETE FROM record_table")
    suspend fun deleteAll()

    @Query("DELETE FROM record_table WHERE id=:recordId")
    suspend fun deleteRecordById(recordId: Int)
}