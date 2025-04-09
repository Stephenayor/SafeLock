package com.example.safelock.domain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.safelock.data.repository.ThemeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultThemeRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) : ThemeRepository {

    companion object {
        private val IS_DARK_THEME_KEY = booleanPreferencesKey("is_dark_theme")
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val isDarkTheme: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_DARK_THEME_KEY] ?: false
        }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    override suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_THEME_KEY] = isDark
        }
    }
}