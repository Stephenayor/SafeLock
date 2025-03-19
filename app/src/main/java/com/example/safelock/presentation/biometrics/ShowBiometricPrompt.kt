package com.example.safelock.presentation.biometrics

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.safelock.utils.biometrics.BiometricPromptManager

@Composable
fun ShowBiometricPrompt(
    title: String?,
    description: String?,
    onResult: (BiometricPromptManager.BiometricResult) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? AppCompatActivity
        ?: throw IllegalStateException("Composable must be hosted in an AppCompatActivity")

    val biometricPromptManager = remember { BiometricPromptManager(activity) }

    LaunchedEffect(Unit) {
        biometricPromptManager.promptResults.collect { result ->
            onResult(result)
        }
    }

    LaunchedEffect(Unit) {
        biometricPromptManager.showBiometricPrompt(title, description)
    }
}
