package com.example.safelock.data.repository

import android.net.Uri
import com.example.safelock.utils.ApiResponse
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface SignUpLoginRepository {
    suspend fun signUpWithEmailPassword(email: String, password: String): Flow<ApiResponse<Unit>>

    suspend fun signInWithEmailPassword(email: String, password: String): Flow<ApiResponse<FirebaseUser>>


     fun saveUserEmail(key: String, value: String)
     fun saveUserPassword(key: String, value: String)
     fun getUserEmail(key: String?): String?
     fun getUserPassword(key: String?): String?
}