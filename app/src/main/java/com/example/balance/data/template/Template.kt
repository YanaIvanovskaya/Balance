package com.example.balance.data.template

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "template_table")
data class Template(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var recordId: Int
)

@Dao
interface TemplateDao {

    @Query("SELECT * FROM template_table")
    fun getAll(): Flow<List<Template>>

//    @Query("SELECT id FROM category_table WHERE name = :name")
//    fun getId(name: String): Flow<Int>
//
//    @Query("SELECT name FROM category_table WHERE id = :id")
//    fun getNameById(id: Int): Flow<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(template: Template)

    @Query("DELETE FROM category_table")
    suspend fun deleteAll()
}