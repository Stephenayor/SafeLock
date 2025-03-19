package com.example.safelock.presentation.securemedia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.safelock.presentation.SecuredMedia
import com.example.safelock.utils.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecureMediaActivity : AppCompatActivity() {

    private val baseViewModel: BaseViewModel by viewModels()
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

            SecuredMedia(
                modifier = Modifier,
                navController = rememberNavController(),
                activity = this
            )
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