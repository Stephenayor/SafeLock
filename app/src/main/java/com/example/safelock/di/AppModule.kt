package com.example.safelock.di

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.safelock.data.repository.SignUpLoginRepository
import com.example.safelock.data.repository.DashBoardRepository
import com.example.safelock.data.repository.ScreenUsageRepository
import com.example.safelock.data.repository.SecuredMediaRepository
import com.example.safelock.data.repository.database.AppDatabase
import com.example.safelock.data.repository.database.dao.SaveImageDao
import com.example.safelock.data.repository.database.dao.ScreenUsageDao
import com.example.safelock.domain.SignUpLoginRepositoryImpl
import com.example.safelock.domain.DashBoardRepositoryImpl
import com.example.safelock.domain.ScreenUsageRepositoryImpl
import com.example.safelock.domain.SecuredMediaRepositoryImpl
import com.example.safelock.utils.AppConstants
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
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
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
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
        context: Context,
        saveImageDao: SaveImageDao
    ): DashBoardRepository{
        return DashBoardRepositoryImpl(firebaseStorage, firebaseFireStore, context, saveImageDao)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideScreenUsageDao(database: AppDatabase): ScreenUsageDao {
        return database.screenUsageDao()
    }

    @Provides
    @Singleton
    fun provideSaveImageDao(database: AppDatabase): SaveImageDao {
        return database.savedImageDao()
    }

    @Provides
    @Singleton
    fun provideScreenUsageRepository(screenUsageDao: ScreenUsageDao): ScreenUsageRepository {
        return ScreenUsageRepositoryImpl(screenUsageDao)
    }

    @Provides
    @Singleton
    fun provideSecuredMediaRepository(saveImageDao: SaveImageDao): SecuredMediaRepository {
        return SecuredMediaRepositoryImpl(saveImageDao)
    }


}