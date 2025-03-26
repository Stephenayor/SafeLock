package com.example.safelock.utils.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.safelock.data.repository.DashBoardRepository
import com.example.safelock.data.repository.ScreenUsageRepository
import com.example.safelock.data.repository.database.entity.SaveImageEntity
import com.example.safelock.data.repository.database.entity.ScreenUsageEntity
import com.example.safelock.utils.ApiResponse
import com.example.safelock.utils.biometrics.NewBiometricPromptManagerClass
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    val analytics: FirebaseAnalytics,
    private val screenUsageRepository: ScreenUsageRepository,
    private val dashBoardRepository: DashBoardRepository
) : ViewModel() {

    private val _mostUsedScreens = MutableStateFlow<List<ScreenUsageEntity>>(emptyList())
    val mostUsedScreens: StateFlow<List<ScreenUsageEntity>> = _mostUsedScreens

    private val _savedImagesInDB =
        MutableStateFlow<ApiResponse<List<SaveImageEntity>>>(ApiResponse.Idle)
    val savedImagesInDB: StateFlow<ApiResponse<List<SaveImageEntity>>> = _savedImagesInDB


    fun onScreenViewed(screenName: String) {
        val bundle = Bundle().apply {
            putString("screen_name", screenName)
        }
        analytics.logEvent("my_custom_screen_view", bundle)
        // Increment local counter
        viewModelScope.launch {
            screenUsageRepository.incrementScreenUsage(screenName)
        }
    }

    fun fetchMostUsedScreens() {
        viewModelScope.launch {
            val screens = screenUsageRepository.getMostUsedScreens()
            _mostUsedScreens.value = screens
        }
    }

    fun saveImagesInDB(imageUrl: String, imageTitle: String, isVideo: Boolean) {
        viewModelScope.launch {
            dashBoardRepository.saveImagesInDB(imageUrl, imageTitle, isVideo)
        }
    }

    fun getSavedImages() {
        _savedImagesInDB.value = ApiResponse.Loading
        viewModelScope.launch {
            dashBoardRepository.getSavedImages().collect { response ->
                _savedImagesInDB.value = response
            }
        }
    }

    private lateinit var promptManager: NewBiometricPromptManagerClass

    // Called once you have an Activity reference
    fun initPromptManager(activity: AppCompatActivity) {
        promptManager = NewBiometricPromptManagerClass(activity)
    }

    fun showBiometricPrompt(
        title: String,
        description: String
    ) {
        promptManager.showBiometricPrompt(
            title = title,
            description = description
        )
    }

    fun getPromptManager(): NewBiometricPromptManagerClass {
        return promptManager
    }

    lateinit var appNavigationController: NavController

}