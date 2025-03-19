package com.example.safelock.data.repository

import com.example.safelock.data.repository.database.entity.ScreenUsageEntity

interface ScreenUsageRepository {

    suspend fun incrementScreenUsage(screenName: String)
    suspend fun getMostUsedScreens(): List<ScreenUsageEntity>
}