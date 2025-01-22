package com.example.safelock.presentation.onboarding

import android.app.Activity
import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.safelock.R
import com.example.safelock.presentation.biometrics.BiometricsActivity
import com.example.safelock.utils.AppConstants
import com.example.safelock.utils.BiometricPromptManager
import com.example.safelock.utils.Route
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun GettingStarted(navController: NavController) {
    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current
            val firebaseAuth = Firebase.auth
            val biometricsLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val isSuccess = result.data?.getBooleanExtra("AUTH_SUCCESS", false) ?: false
                        if (isSuccess) {
                            navController.navigate(Route.DASHBOARD)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.investment_go),
                contentDescription = "Investment Illustration",
                modifier = Modifier
                    .height(700.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("Sign up") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color.Unspecified
                ),

                shape = RoundedCornerShape(8.dp)
            )
            { Text(text = "Get Started") }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val currentUser = firebaseAuth.currentUser
                    if (currentUser != null) {
                        val intent = Intent(context, BiometricsActivity::class.java).apply {
                            putExtra(AppConstants.USER_UID, currentUser.uid)
                        }
                        biometricsLauncher.launch(intent)
                    } else {
                        navController.navigate("Login")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFF4C0A8A),
                        shape = RoundedCornerShape(8.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Sign in")
            }

            val appCompatActivity = AppCompatActivity()
            val promptManager by lazy {
                BiometricPromptManager(appCompatActivity)
            }
            val biometricResult by promptManager.promptResults.collectAsState(
                initial = null
            )
            val enrollLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = {
                    println("Activity result: $it")
                }
            )
            LaunchedEffect(biometricResult) {
                if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
                    if (Build.VERSION.SDK_INT >= 30) {
                        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                            )
                        }
                        enrollLauncher.launch(enrollIntent)
                    }
                }
            }

            biometricResult?.let { result ->
                // Handle side effects using LaunchedEffect
                if (result is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Route.DASHBOARD)

                    }
                }

                Text(
                    text = when (result) {
                        is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                            result.error
                        }

                        BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                            "Authentication failed"
                        }

                        BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                            "Authentication not set"
                        }

                        BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                            "Authentication success"
                        }

                        BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                            "Feature unavailable"
                        }

                        BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                            "Hardware unavailable"
                        }
                    }
                )

            }
        }
    }
}