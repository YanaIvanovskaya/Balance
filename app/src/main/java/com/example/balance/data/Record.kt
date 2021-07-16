package com.example.balance.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Entity(tableName = "record_table")
data class Record(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "record_id") var id: Long,
    var sumOfMoney: Int,
    var kindRecord: Int,
    var kindMoney:  Int,
) {

}

@Dao
interface RecordDao {
//    @Query("SELECT * FROM record_table ORDER BY word ASC")
//    fun getAlphabetizedWords(): List<Word>

    @Query("SELECT * FROM record_table")
    fun getAllRecords(): Flow<List<Record>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: Record)

//    @Query("DELETE FROM record_table")
//    suspend fun deleteAll()
}