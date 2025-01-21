package com.example.safelock.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safelock.presentation.SplashScreen
import com.example.safelock.presentation.biometrics.SetupBiometricsScreen
import com.example.safelock.presentation.dashboard.DashBoardScreen
import com.example.safelock.presentation.login.LoginScreen
import com.example.safelock.presentation.onboarding.GettingStarted
import com.example.safelock.presentation.onboarding.SignUpScreen
import com.example.safelock.utils.Route
import com.example.safelock.utils.dialog.BiometricsAuthenticationDialog

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Splash screen") {
        composable("Splash screen"){
            SplashScreen(navController)
        }
        composable("Getting started"){
            GettingStarted(navController)
        }
        composable("Sign up"){
            SignUpScreen(navController)
        }
        composable(Route.LOGIN){
          LoginScreen(navController)
        }
        composable(Route.SETUP_BIOMETRICS){
            val activity = AppCompatActivity()
            SetupBiometricsScreen(
                activity, navController
            )
        }
        composable(Route.DASHBOARD){
           DashBoardScreen()
        }
        composable("biometricsauthenticationdialog"){
            BiometricsAuthenticationDialog(modifier = Modifier,"", onCancelBiometricsDialog = {})
        }
    }
}