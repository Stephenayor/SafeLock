package com.example.safelock.data.repository

import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {
    val isDarkTheme: StateFlow<Boolean>
    suspend fun setDarkTheme(isDark: Boolean)
}