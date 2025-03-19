package com.example.safelock.utils.Firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class EventTracker {
    companion object{

        fun trackEvent(screenName: String, screenClass: String): Bundle{
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
            }
            return bundle
        }
    }
}