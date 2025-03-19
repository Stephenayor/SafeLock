package com.example.safelock.presentation.onboarding

import android.annotation.SuppressLint
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.safelock.R
import com.example.safelock.presentation.AnalyticsViewModel
import com.example.safelock.presentation.biometrics.BiometricsActivity
import com.example.safelock.utils.AppConstants
import com.example.safelock.utils.biometrics.BiometricPromptManager
import com.example.safelock.utils.Firebase.EventTracker
import com.example.safelock.utils.Route
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.auth

@Composable
fun GettingStarted(
    navController: NavController,
    analyticsViewModel: AnalyticsViewModel = hiltViewModel()
) {

    val firebaseAnalytics = analyticsViewModel.analytics
    trackScreen("GettingStarted", analyticsViewModel)

    DisposableEffect(Unit) {
        onDispose {
            val bundle =
                EventTracker.trackEvent("GettingStartedScreenDisposable", "Getting")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

    LaunchedEffect(Unit) {
        val bundle =
            EventTracker.trackEvent("GettingStartedScreenLaunch", "GettingStartedScreenLaunch")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
    ) { innerPadding ->
        Modifier.padding(innerPadding)
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
                            navController.navigate(Route.HOME_SCREEN) {
                                popUpTo(Route.LOGIN) { inclusive = true }
                            }
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
                            putExtra(AppConstants.GETTING_STARTED, true)
                        }
                        biometricsLauncher.launch(intent)
                    } else {
                        navController.navigate(Route.LOGIN)
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

        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun trackScreen(name: String, analyticsViewModel: AnalyticsViewModel) {
    DisposableEffect(Unit) {
        onDispose {
            val bundle = EventTracker.trackEvent("GettingStartedScreen", name)
            analyticsViewModel.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }
}



@SuppressLint("ComposableNaming")
@Composable
fun trackEvent(name: String, analyticsViewModel: AnalyticsViewModel) {
    DisposableEffect(Unit) {
        onDispose {
            val bundle = EventTracker.trackEvent("GettingStartedScreen", "GettingStarted")
            analyticsViewModel.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
        }
    }
}