package com.example.safelock

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.safelock.navigation.Navigation
import com.example.safelock.presentation.securemedia.SecureMediaActivity
import com.example.safelock.ui.theme.SafeLockTheme
import com.example.safelock.ui.theme.ThemeViewModel
import com.example.safelock.utils.SessionTimeoutWrapper
import com.example.safelock.utils.Tools
import com.example.safelock.utils.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Tools.showToast(this, "Notification permission granted")
            } else {
                Tools.showToast(this, "Notification permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SessionTimeoutWrapper {
                val darkTheme by themeViewModel.theme.collectAsState(initial = false)
                SafeLockTheme(darkTheme = darkTheme) {
                    CheckForNotificationPermission()
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Navigation(themeViewModel)
                            }

                        }

                    }


                }

            }
        }
    }


    @Composable
    fun CheckForNotificationPermission() {
        // Only needed on Android 13 (API 33) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                val isGranted = ContextCompat.checkSelfPermission(
                    context, permission
                ) == PackageManager.PERMISSION_GRANTED
                if (!isGranted) {
                    requestNotificationPermissionLauncher.launch(permission)
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            with(Intent(context, MainActivity::class.java)) {
                context.startActivity(this)
            }
        }

    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SafeLockTheme {
        Greeting("Android")
    }
}
