package com.example.safelock.presentation.biometrics

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import com.example.safelock.utils.biometrics.BiometricPromptManager
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