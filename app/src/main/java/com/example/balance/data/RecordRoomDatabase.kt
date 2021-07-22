package com.example.balance.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Record::class, Category::class, Template::class], version = 6, exportSchema = false)
abstract class RecordRoomDatabase : RoomDatabase() {

    abstract fun RecordDao(): RecordDao

    abstract fun CategoryDao(): CategoryDao

    abstract fun TemplateDao(): TemplateDao

    private class RecordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.RecordDao())
                }
            }
        }

        suspend fun populateDatabase(recordDao: RecordDao) {
            recordDao.deleteAll()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RecordRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): RecordRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecordRoomDatabase::class.java,
                    "record_database"
                )
                    .addCallback(RecordDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}