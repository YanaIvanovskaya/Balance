package com.example.balance.data.template

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "template_table")
data class Template(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var recordId: Int,
    var frequencyOfUse: Int = 0
)

@Dao
interface TemplateDao {

    @Query("SELECT * FROM template_table")
    fun getAll(): Flow<List<Template>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(template: Template)

    @Query("DELETE FROM template_table WHERE recordId=:recordId")
    suspend fun deleteTemplateByRecordId(recordId: Int)
}