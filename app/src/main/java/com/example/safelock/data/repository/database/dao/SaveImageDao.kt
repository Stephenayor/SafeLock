package com.example.safelock.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.safelock.data.repository.database.entity.SaveImageEntity
import com.example.safelock.data.repository.database.entity.ScreenUsageEntity
import com.example.safelock.utils.ApiResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface SaveImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveImages(saveImageEntity: SaveImageEntity)

    @Query("SELECT * FROM saved_image")
    suspend fun getSavedImages(): List<SaveImageEntity>

    // Delete a single image by its title
    @Query("DELETE FROM saved_image WHERE imageTitle = :title")
    suspend fun deleteImageByTitle(title: String)

    // Delete all images
    @Query("DELETE FROM saved_image")
    suspend fun deleteAllImages()
}