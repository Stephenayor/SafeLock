package com.example.safelock.presentation.biometrics

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.safelock.R
import com.example.safelock.utils.biometrics.BiometricPromptManager

@Composable
fun CustomBiometricPrompt(
    biometricPromptManager: BiometricPromptManager,
    onAuthenticationSuccess: () -> Unit,
    onAuthenticationFailure: (String) -> Unit,
    title: String? = null,
    onCancel: () -> Unit
) {

//    val context = LocalContext.current
//    val activity = context as? AppCompatActivity
//        ?: throw IllegalStateException("BiometricPrompt requires AppCompatActivity context")


//    val promptManager by lazy {
//        BiometricPromptManager(activity)
//    }
    // State to manage the visibility of the biometric prompt UI
    var showPrompt by remember { mutableStateOf(true) }
    val modifier: Modifier

    if (showPrompt) {
        // Display the custom UI
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = title ?: "Authentication",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "Confirm your fingerprint to continue",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace
                )

                Image(
                    painter = painterResource(id = R.drawable.fingerprintsensor),
                    contentDescription = "Fingerprint",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(25.dp))

                // Divider
                Divider(color = Color.LightGray, thickness = 1.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Cancel Button
                    TextButton(onClick = {
                        showPrompt = false
                        onCancel()
                    }) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Try Again Button
                    Button(onClick = {
                        showPrompt = false
                        biometricPromptManager.showBiometricPrompt(
                            title = "Authenticate",
                            description = "Use your fingerprint to authenticate."
                        )
                    }) {
                        Text("Authenticate")
                    }
                }
            }
        }
    }

    // Observe results from the biometric manager
    LaunchedEffect(Unit) {
        biometricPromptManager.promptResults.collect { result ->
            when (result) {
                is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                    onAuthenticationSuccess()
                }
                is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                    onAuthenticationFailure(result.error)
                }
                is BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                    onAuthenticationFailure("Authentication failed. Try again.")
                }
                else -> {
                    onAuthenticationFailure("Biometric authentication unavailable.")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomBiometricPromptPreview() {
    // Create a mock BiometricPromptManager
    val mockManager = BiometricPromptManager(activity = AppCompatActivity())

    CustomBiometricPrompt(
        biometricPromptManager = mockManager,
        onAuthenticationSuccess = { /* Do nothing in preview */ },
        onAuthenticationFailure = { /* Do nothing in preview */ },
        title = "Preview Biometric Prompt",
        onCancel = { /* Handle cancel in preview */ }
    )
}


