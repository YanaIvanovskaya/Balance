package com.example.balance.data

import androidx.room.*
import androidx.room.ColumnInfo.INTEGER
import kotlinx.coroutines.flow.Flow
import java.util.*

@Entity(tableName = "record_table")
data class Record(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var sumOfMoney: Int,
    var category: String,
    var date: String
//    var kindRecord: Int
//    var kindMoney:  Int,
)

@Dao
interface RecordDao {

    @Query("SELECT * FROM record_table")
    fun getAllRecords(): Flow<List<Record>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: Record)

    @Query("DELETE FROM record_table")
    suspend fun deleteAll()
}