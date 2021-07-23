package com.example.balance.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

enum class CategoryType {
    CATEGORY_COSTS,
    CATEGORY_PROFIT
}

@Entity(tableName = "category_table")
data class Category(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var type: CategoryType
)

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category_table WHERE type = :type")
    fun getAll(type: CategoryType): Flow<List<Category>>

    @Query("SELECT id FROM category_table WHERE name = :name")
    fun getId(name: String): Int

    @Query("SELECT name FROM category_table WHERE id = :id")
    fun getNameById(id: Int): Flow<String>

    @Query("SELECT id, name from category_table")
    fun getCategoryWith(): List<CategoryWithRecords?>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category)

    @Query("DELETE FROM category_table")
    suspend fun deleteAll()
}