package com.example.safelock.utils.Firebase

import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface FirebaseAnalyticsEntryPoint {
    fun getFirebaseAnalytics(): FirebaseAnalytics
}
