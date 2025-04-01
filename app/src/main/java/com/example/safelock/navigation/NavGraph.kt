package com.example.safelock.navigation

import SplashScreen
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.safelock.presentation.securemedia.SecuredMedia
import com.example.safelock.presentation.biometrics.SetupBiometricsScreen
import com.example.safelock.presentation.dashboard.DashBoardScreen
import com.example.safelock.presentation.home.HomeScreen
import com.example.safelock.presentation.location.LocationComposable
import com.example.safelock.presentation.login.LoginScreen
import com.example.safelock.presentation.onboarding.GettingStarted
import com.example.safelock.presentation.onboarding.SignUpScreen
import com.example.safelock.presentation.videoplayer.VideoPlayerScreen
import com.example.safelock.utils.Route
import com.example.safelock.utils.base.BaseViewModel
import com.example.safelock.utils.dialog.BiometricsAuthenticationDialog

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Splash screen") {
        composable("Splash screen") {
            SplashScreen(navController)
        }
        composable("Getting started") {
            GettingStarted(navController)
        }
        composable("Sign up") {
            SignUpScreen(navController)
        }
        composable(Route.LOGIN) {
            LoginScreen(navController)
        }
        composable(Route.SETUP_BIOMETRICS) {
            val activity = AppCompatActivity()
            SetupBiometricsScreen(
                activity, navController
            )
        }
        composable(Route.DASHBOARD) {
            DashBoardScreen(modifier = Modifier, navController)
        }
        composable(Route.SECURED_MEDIA){
            val activity = AppCompatActivity()
            SecuredMedia(modifier = Modifier, navController, activity)
        }
        composable(Route.HOME_SCREEN){
            HomeScreen(modifier = Modifier, navController)
        }
        composable(Route.LOCATION){
            LocationComposable()
        }
        composable("biometricsauthenticationdialog"){
            BiometricsAuthenticationDialog(modifier = Modifier,"", onCancelBiometricsDialog = {})
        }
        composable(
            route = "video_player_screen?videoUrl={videoUrl}",
            arguments = listOf(navArgument("videoUrl") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            // Retrieve the video URL; if it's null, it will default to ""
            val videoUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
            VideoPlayerScreen(videoUrl = videoUrl, navController = navController)
        }


    }
}



@Composable
fun SecureMediaNavigation(modifier: Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.SECURED_MEDIA){
        val appCompatActivity = AppCompatActivity()

        composable(Route.SECURED_MEDIA) {
            SecuredMedia(modifier, navController, appCompatActivity)
        }

        composable(
            route = "video_player_screen?videoUrl={videoUrl}",
            arguments = listOf(navArgument("videoUrl") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            })
        ) { backStackEntry ->
            // Retrieve the video URL; if it's null, it will default to ""
            val videoUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
            VideoPlayerScreen(videoUrl = videoUrl, navController = navController)
        }


    }

}
