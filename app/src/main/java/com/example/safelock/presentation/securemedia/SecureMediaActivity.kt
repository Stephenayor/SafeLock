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
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.safelock.presentation.videoplayer.VideoPlayerScreen
import com.example.safelock.utils.Route
import com.example.safelock.utils.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecureMediaActivity : AppCompatActivity() {

    private val baseViewModel: BaseViewModel by viewModels()
    private var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        baseViewModel.initPromptManager(this)

        setContent {
            androidx.compose.material3.MaterialTheme {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.White)
                ) {
                    // Intentionally left empty: no app bar, no content
                }
            }

            SecureMediaNavigation()
        }
    }

    @Composable
    private fun SecureMediaNavigation() {
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
                    activity = this@SecureMediaActivity
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
                Log.d("navC", rawVideoUrl)
                VideoPlayerScreen(videoUrl = finalVideoUrl, navController)
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    companion object {
        fun start(context: Context) {
            with(Intent(context, SecureMediaActivity::class.java)) {
                context.startActivity(this)
            }
        }

        fun finish() {
            finish()
        }
    }
}