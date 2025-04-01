package com.example.safelock

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.safelock.navigation.Navigation
import com.example.safelock.ui.theme.SafeLockTheme
import com.example.safelock.utils.SessionTimeoutWrapper
import com.example.safelock.utils.Tools
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
                SafeLockTheme {
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
                                Navigation()
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