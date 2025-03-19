package com.example.safelock.data.repository

import android.net.Uri
import com.example.safelock.data.repository.database.entity.SaveImageEntity
import com.example.safelock.data.repository.database.entity.ScreenUsageEntity
import com.example.safelock.data.repository.model.MediaData
import com.example.safelock.utils.ApiResponse
import kotlinx.coroutines.flow.Flow

interface DashBoardRepository {

    suspend fun uploadImageToCloud(imageUri: Uri): Flow<ApiResponse<Uri>>

    suspend fun uploadVideoToCloud(videoUri: Uri): Flow<ApiResponse<Uri>>

    suspend fun uploadMediaDataToFireStore(imageDownloadUrl: Uri, mediaDataTitle: String): Flow<ApiResponse<Boolean>>

    suspend fun getMediaItems(): Flow<ApiResponse<List<MediaData>>>

    suspend fun saveImagesInDB(imageUrl: String, imageTitle: String)
    suspend fun getSavedImages(): Flow<ApiResponse<List<SaveImageEntity>>>
}