package com.example.safelock.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import com.example.safelock.data.repository.database.entity.ScreenUsageEntity
import com.example.safelock.data.repository.model.DrawerFeature

class Tools {
    companion object{
        fun showToast(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun mapToDrawerFeatures(screenUsages: List<ScreenUsageEntity>): List<DrawerFeature> {
            return screenUsages.map { usage ->
                val icon = when (usage.screenName) {
                    "DashBoard" -> Icons.Filled.Home
                    "SecuredMedia" -> Icons.Filled.PermMedia
                    "Profile" -> Icons.Filled.Person
                    else -> Icons.Filled.Star
                }
                DrawerFeature(usage.screenName, icon)
            }
        }

    }
}