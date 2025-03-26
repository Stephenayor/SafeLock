package com.example.safelock.data.repository

import com.example.safelock.utils.ApiResponse
import kotlinx.coroutines.flow.Flow

interface SecuredMediaRepository {

    suspend fun deleteImageByTitle(title: String) : Flow<ApiResponse<Boolean>>
    suspend fun deleteAllImages() : Flow<ApiResponse<Boolean>>
}