package com.example.safelock.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safelock.presentation.SplashScreen
import com.example.safelock.presentation.login.LoginScreen
import com.example.safelock.presentation.onboarding.GettingStarted
import com.example.safelock.presentation.onboarding.SignUpScreen

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
        composable("Login"){
          LoginScreen()
        }
    }
}