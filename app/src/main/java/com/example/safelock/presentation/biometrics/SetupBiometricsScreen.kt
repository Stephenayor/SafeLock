package com.example.safelock.presentation.biometrics

import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.safelock.utils.BiometricPromptManager
import com.example.safelock.utils.Route

@Composable
fun SetupBiometricsScreen(activity: AppCompatActivity, navController: NavController) {

    val biometricPromptManager = remember { BiometricPromptManager(activity) }


    CustomBiometricPrompt(
        biometricPromptManager = biometricPromptManager,
        onAuthenticationSuccess = {
            navController.navigate(Route.DASHBOARD)
        },
        onAuthenticationFailure = { error ->
            // Show an error dialog or message
            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
        },
        onCancel = {
            // Handle cancellation, e.g., navigate back
            navController.popBackStack()
        }
    )
}