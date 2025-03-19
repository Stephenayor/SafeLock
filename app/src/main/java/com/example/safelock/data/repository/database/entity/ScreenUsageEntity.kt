package com.example.safelock.data.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screen_usage")
data class ScreenUsageEntity(
    @PrimaryKey val screenName: String,
    val usageCount: Int
)
