package com.example.safelock.presentation.securemedia

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.safelock.presentation.dashboard.DashBoardScreen
import com.example.safelock.presentation.home.HomeScreen
import com.example.safelock.presentation.videoplayer.VideoPlayerScreen
import com.example.safelock.ui.theme.SafeLockTheme
import com.example.safelock.ui.theme.ThemeViewModel
import com.example.safelock.utils.Route
import com.example.safelock.utils.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecureMediaActivity : AppCompatActivity() {

    private val baseViewModel: BaseViewModel by viewModels()
    private lateinit var secureNavController: NavController
    private val themeViewModel: ThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        baseViewModel.initPromptManager(this)

        setContent {
            val darkTheme by themeViewModel.theme.collectAsState(initial = false)
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color.White)
            ) {
                // Intentionally left empty: no app bar, no content
            }

//            SecureMediaNavigation(themeViewModel)
            SecureMediaNavigation(themeViewModel = themeViewModel) { navController ->
                secureNavController = navController
            }
        }
    }

    @Composable
    private fun SecureMediaNavigation(
        themeViewModel: ThemeViewModel,
        onNavControllerCreated: (NavController) -> Unit
    ) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.SECURED_MEDIA
        ) {
            // Define the secured media screen destination
            composable(Route.SECURED_MEDIA) {
                SecuredMedia(
                    modifier = Modifier,
                    navController = navController,
                    activity = this@SecureMediaActivity,
                    themeViewModel
                )
            }

            composable(
                route = "video_player_screen?{videoUrl}",
                arguments = listOf(navArgument("videoUrl") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                })
            ) { backStackEntry ->
                val rawVideoUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
                val finalVideoUrl =
                    if (rawVideoUrl.contains("%")) rawVideoUrl else Uri.encode(rawVideoUrl, "/:?&=")
                VideoPlayerScreen(videoUrl = finalVideoUrl, navController)
            }

            composable(Route.HOME_SCREEN) {
                HomeScreen(modifier = Modifier, navController, themeViewModel)
            }

        }
    }

    override fun onBackPressed() {
        val currentRoute = secureNavController.currentBackStackEntry?.destination?.route
        if (currentRoute?.startsWith("video_player_screen") == true) {
            // Instead of letting back pop the video player,
            // navigate to HomeScreen.
            secureNavController.navigate(Route.HOME_SCREEN) {
                // Clear the back stack so that pressing back again does not return to video player.
                popUpTo(Route.HOME_SCREEN) { inclusive = true }
            }
        } else if (currentRoute == Route.SECURED_MEDIA) {
            secureNavController.navigate(Route.HOME_SCREEN) {
                popUpTo(Route.HOME_SCREEN) { inclusive = true }
            }
        } else {
            // Otherwise, call default behavior.
            super.onBackPressed()
        }
    }


    companion object {
        fun start(context: Context) {
            with(Intent(context, SecureMediaActivity::class.java)) {
                context.startActivity(this)
            }
        }
    }
}