package com.example.safelock.data.repository

import com.example.safelock.utils.ApiResponse
import kotlinx.coroutines.flow.Flow

interface SignUpLoginRepository {
    suspend fun signUpWithEmailPassword(email: String, password: String): Flow<ApiResponse<Unit>>
}