package com.example.safelock.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.safelock.data.repository.database.entity.ScreenUsageEntity

@Dao
interface ScreenUsageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUsage(screenUsage: ScreenUsageEntity)

    @Query("SELECT * FROM screen_usage ORDER BY usageCount DESC")
    suspend fun getMostUsedScreens(): List<ScreenUsageEntity>
}
