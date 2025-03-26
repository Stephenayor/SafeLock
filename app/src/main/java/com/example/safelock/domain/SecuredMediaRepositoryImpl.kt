package com.example.safelock.domain

import com.example.safelock.data.repository.SecuredMediaRepository
import com.example.safelock.data.repository.database.dao.SaveImageDao
import com.example.safelock.utils.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SecuredMediaRepositoryImpl @Inject constructor(
    private val saveImageDao: SaveImageDao
) : SecuredMediaRepository {
    override suspend fun deleteImageByTitle(title: String): Flow<ApiResponse<Boolean>> = flow {
        emit(ApiResponse.Loading)
            try {
                saveImageDao.deleteImageByTitle(title)
                emit(ApiResponse.Success(true))
            } catch (exception: Exception) {
                emit(ApiResponse.Failure(exception))
            }
    }


    override suspend fun deleteAllImages(): Flow<ApiResponse<Boolean>> = flow {
        emit(ApiResponse.Loading)
            try {
                saveImageDao.deleteAllImages()
                emit(ApiResponse.Success(true))
            } catch (exception: Exception) {
                emit(ApiResponse.Failure(exception))
            }
    }

}