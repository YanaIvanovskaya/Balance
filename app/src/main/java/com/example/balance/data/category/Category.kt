package com.example.balance.data.category

import androidx.room.*
import com.example.balance.data.record.Record
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate
import java.util.*

enum class CategoryType {
    CATEGORY_COSTS,
    CATEGORY_PROFIT
}

@Entity(tableName = "category_table")
data class Category(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var dateCreation: String = Date().toString(),
    var day: Int? = currentDate.dayOfMonth,
    var month: Int? = currentDate.month.value,
    var year: Int? = currentDate.year,
    var weekDay: Int? = currentDate.dayOfWeek.value,

    var name: String,
    var type: CategoryType,
    var isDeleted: Boolean? = false

    ) {
    companion object {
        private var currentDate: LocalDate = LocalDate.now()
    }
}

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category_table WHERE type = :type AND isDeleted=0")
    fun getCategoriesByType(type: CategoryType): Flow<List<Category>>

    @Query("SELECT * FROM category_table WHERE isDeleted=0")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT id FROM category_table WHERE name = :name")
    fun getId(name: String): Int

    @Query("SELECT name FROM category_table WHERE id = :id")
    fun getNameById(id: Int): Flow<String>

    @Query("SELECT id, name from category_table")
    fun getCategoryWith(): List<CategoryWithRecords?>?

    @Query("SELECT name from category_table WHERE type=:categoryType")
    fun getCategoryNames(categoryType: CategoryType): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category)

    @Query("DELETE FROM category_table")
    suspend fun deleteAll()

    @Query("UPDATE category_table SET isDeleted=1 WHERE id=:id")
    suspend fun deleteCategoryById(id:Int)
}