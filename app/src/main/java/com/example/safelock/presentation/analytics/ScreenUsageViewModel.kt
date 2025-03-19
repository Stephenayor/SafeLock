package com.example.safelock.presentation.analytics

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelock.data.repository.ScreenUsageRepository
import com.example.safelock.data.repository.database.entity.ScreenUsageEntity
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ScreenUsageViewModel @Inject constructor(
    private val analytics: FirebaseAnalytics,
    private val screenUsageRepository: ScreenUsageRepository
) : ViewModel() {

    private val _mostUsedScreens = MutableStateFlow<List<ScreenUsageEntity>>(emptyList())
    val mostUsedScreens: StateFlow<List<ScreenUsageEntity>> = _mostUsedScreens

    fun onScreenViewed(screenName: String) {
        // Log custom event to Firebase
        val bundle = Bundle().apply {
            putString("screen_name", screenName)
        }
        analytics.logEvent("my_custom_screen_view", bundle)

        // Increment local usage
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
}
