package com.example.safelock.domain

import com.example.safelock.data.repository.SignUpLoginRepository
import com.example.safelock.utils.ApiResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SignUpLoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): SignUpLoginRepository {


    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String
    ): Flow<ApiResponse<Unit>> = flow{
        emit(ApiResponse.Loading)
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            emit(ApiResponse.Success(Unit))
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e))
        }
    }
}