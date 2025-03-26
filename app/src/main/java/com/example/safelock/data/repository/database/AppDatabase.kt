package com.example.safelock.data.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.safelock.data.repository.database.dao.SaveImageDao
import com.example.safelock.data.repository.database.dao.ScreenUsageDao
import com.example.safelock.data.repository.database.entity.SaveImageEntity
import com.example.safelock.data.repository.database.entity.ScreenUsageEntity

@Database(
    entities = [ScreenUsageEntity::class, SaveImageEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun screenUsageDao(): ScreenUsageDao
    abstract fun savedImageDao(): SaveImageDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS saved_image(" +
                            "imageUrl TEXT NOT NULL, " +
                            "imageTitle TEXT NOT NULL DEFAULT 'Untitled'," +
                            "PRIMARY KEY(imageUrl)" +
                            ")"
                )
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add a new column "isVideo" to the saved_image table.
                // INTEGER is used to represent Boolean values (0 = false, 1 = true).
                db.execSQL("ALTER TABLE saved_image ADD COLUMN isVideo INTEGER NOT NULL DEFAULT 0")
            }
        }



        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "safelock_app_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
