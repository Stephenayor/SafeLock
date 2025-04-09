package com.example.safelock.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safelock.data.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
): ViewModel() {


    val theme: StateFlow<Boolean> = themeRepository.isDarkTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            themeRepository.setDarkTheme(isDark)
        }
    }

    private fun isSystemInDarkThemeDefault(): Boolean {
        // Return a default value; we can base this on a saved preference.
        return false
    }

}