package com.example.safelock.domain

import android.content.SharedPreferences
import com.example.safelock.data.repository.SignUpLoginRepository
import com.example.safelock.utils.ApiResponse
import com.example.safelock.utils.AppConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SignUpLoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val encryptedPrefs: SharedPreferences
) : SignUpLoginRepository {


    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String
    ): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(ApiResponse.Success(Unit))
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e))
        }
    }

    override suspend fun signInWithEmailPassword(
        email: String,
        password: String
    ): Flow<ApiResponse<FirebaseUser>> = flow {
        emit(ApiResponse.Loading)
        try {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user ?: throw Exception("User is null")
                emit(ApiResponse.Success(user))
            } else {
                throw IllegalArgumentException(AppConstants.GENERIC_ERROR_MSG)
            }
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e))
        }
    }

    override  fun saveUserEmail(key: String, value: String){
        encryptedPrefs.edit().putString(key, value).apply()
    }


    override  fun saveUserPassword(key: String, value: String){
        encryptedPrefs.edit().putString(key, value).apply()
    }

    override  fun getUserEmail(key: String?): String? {
        return encryptedPrefs.getString(key, null)
    }

    override  fun getUserPassword(key: String?): String? {
        return encryptedPrefs.getString(key, null)
    }

}