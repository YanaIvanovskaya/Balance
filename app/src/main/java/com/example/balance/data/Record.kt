package com.example.balance.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

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
    var day: Int = localDate.dayOfMonth,
    var month: String = months[localDate.month.value] ?: "",
    var year: Int = localDate.year,
    var weekDay: String = weekDays[localDate.dayOfWeek.value] ?: "",
    val isVisible: Boolean = true,
    val isImportant: Boolean = true,
    val date: String = "$weekDay $day $month $year",

    var sumOfMoney: Int,
    var recordType: RecordType,
    var moneyType: MoneyType,
    @ColumnInfo(name = "category_id") var categoryId: Int,
    var category: String,
    var comment: String
) {
    companion object {
        private val localDate: LocalDate = LocalDate.now()
        private val months = mapOf(
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

//    @Query("SELECT * FROM record_table WHERE ")
//    fun getRecords(type: RecordType): Flow<List<Record>>

    @Insert
    suspend fun insert(record: Record)

    @Query("DELETE FROM record_table")
    suspend fun deleteAll()
}