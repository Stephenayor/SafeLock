package com.example.safelock.domain

import com.example.safelock.data.repository.ScreenUsageRepository
import com.example.safelock.data.repository.database.dao.ScreenUsageDao
import com.example.safelock.data.repository.database.entity.ScreenUsageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScreenUsageRepositoryImpl(private val dao: ScreenUsageDao): ScreenUsageRepository {
    override suspend fun incrementScreenUsage(screenName: String) {
        withContext(Dispatchers.IO) {
            // Get current usage count
            val current = dao.getMostUsedScreens().find { it.screenName == screenName }
            val newCount = (current?.usageCount ?: 0) + 1

            // Insert or update
            dao.insertOrUpdateUsage(ScreenUsageEntity(screenName, newCount))
        }
    }

    override suspend fun getMostUsedScreens(): List<ScreenUsageEntity> {
        return withContext(Dispatchers.IO) {
            dao.getMostUsedScreens()
        }
    }


}