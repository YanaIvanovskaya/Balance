package com.example.balance.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.balance.data.category.Category
import com.example.balance.data.category.CategoryDao
import com.example.balance.data.record.Record
import com.example.balance.data.record.RecordDao
import com.example.balance.data.template.Template
import com.example.balance.data.template.TemplateDao
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [Record::class, Category::class, Template::class],
    version = 2,
    exportSchema = false
)
abstract class BalanceDatabase : RoomDatabase() {

    abstract fun RecordDao(): RecordDao

    abstract fun CategoryDao(): CategoryDao

    abstract fun TemplateDao(): TemplateDao

    private class BalanceDatabaseCallback : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
        }

    }

    companion object {
        @Volatile
        private var INSTANCE: BalanceDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): BalanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BalanceDatabase::class.java,
                    "balance_database"
                )
                    .addCallback(BalanceDatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}