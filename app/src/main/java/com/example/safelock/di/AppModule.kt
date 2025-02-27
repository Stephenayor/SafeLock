package com.example.safelock.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.safelock.data.repository.SignUpLoginRepository
import com.example.safelock.data.repository.DashBoardRepository
import com.example.safelock.domain.SignUpLoginRepositoryImpl
import com.example.safelock.domain.DashBoardRepositoryImpl
import com.example.safelock.utils.AppConstants
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseRealtimeDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseCloudStorage(): FirebaseStorage {
        return Firebase.storage
    }

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            AppConstants.ENCRYPTED_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideSignUpLoginRepository(
        firebaseAuth: FirebaseAuth,
        sharedPreferences: SharedPreferences
    ): SignUpLoginRepository{
        return SignUpLoginRepositoryImpl(firebaseAuth, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideDashBoardRepository(
        firebaseStorage: FirebaseStorage,
        firebaseFireStore: FirebaseFirestore,
        context: Context
    ): DashBoardRepository{
        return DashBoardRepositoryImpl(firebaseStorage, firebaseFireStore, context)
    }
}