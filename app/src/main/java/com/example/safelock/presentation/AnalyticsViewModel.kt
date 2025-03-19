package com.example.safelock.presentation

import androidx.lifecycle.ViewModel
import com.example.safelock.utils.base.BaseViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    val analytics: FirebaseAnalytics
) : ViewModel()
