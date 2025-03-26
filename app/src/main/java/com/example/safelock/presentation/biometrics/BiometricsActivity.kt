package com.example.safelock.presentation.biometrics

import android.app.Activity
import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.safelock.R
import com.example.safelock.presentation.login.LoginViewModel
import com.example.safelock.ui.theme.SafeLockTheme
import com.example.safelock.utils.AppConstants
import com.example.safelock.utils.Tools
import com.example.safelock.utils.biometrics.BiometricPromptManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class BiometricsActivity : AppCompatActivity() {
    private val promptManager by lazy {
        BiometricPromptManager(this)
    }
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var viewModel: LoginViewModel
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var userId: String
    private  var isFromGettingStarted: Boolean? = false
    private  var isAuthenticated : Boolean = false
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeLockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    userId  = intent.getStringExtra(AppConstants.USER_UID).toString()
                    isFromGettingStarted = intent.getBooleanExtra(AppConstants.GETTING_STARTED, false)
                    isAuthenticated = intent.
                    getBooleanExtra("LoginSuccessful", false)
                    Log.d("biometrics", userId)
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var showPrompt by remember { mutableStateOf(true) }
                        val modifier: Modifier

                        if (showPrompt) {
                            // Display the custom UI
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .clip(
                                            RoundedCornerShape(8.dp)
                                        )
                                        .background(Color.White)
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        modifier = Modifier.padding(top = 8.dp),
                                        text = "Authentication",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.Black,
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    Text(
                                        modifier = Modifier.padding(top = 8.dp),
                                        text = "Confirm your fingerprint to continue",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    Image(
                                        painter = painterResource(id = R.drawable.fingerprintsensor),
                                        contentDescription = "Fingerprint",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .padding(bottom = 16.dp),
                                        contentScale = ContentScale.Fit
                                    )

                                    Spacer(modifier = Modifier.height(25.dp))

                                    // Divider
                                    Divider(color = Color.LightGray, thickness = 1.dp)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 5.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        // Cancel Button
                                        TextButton(onClick = {
                                            showPrompt = false
                                            finish()
                                        }) {
                                            Text(
                                                text = "Cancel",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }


                                        Button(onClick = {
                                            showPrompt = false
                                            // Trigger the biometric prompt
                                            promptManager.showBiometricPrompt(
                                                title = "Authenticate",
                                                description = "Use your fingerprint to authenticate."
                                            )
                                        }) {
                                            Text("Authenticate")
                                        }
                                    }
                                }
                            }
                        }


                        biometricResult?.let { result ->
                            if (result is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
                                LaunchedEffect(Unit) {
                                    if (isFromGettingStarted as Boolean){
                                        val intent = Intent().apply {
                                            putExtra("AUTH_SUCCESS", true)
                                        }
                                        setResult(Activity.RESULT_OK, intent)
                                        finish()
                                    }else{
                                        val intent = Intent().apply {
                                            putExtra("AUTH_SUCCESS", true)
                                        }
                                        setResult(Activity.RESULT_OK, intent)
                                        finish()
                                        createUserDetailsOnFirebase()
                                    }
                                }
                            }

                            if (result is BiometricPromptManager.BiometricResult.AuthenticationError) {
                                if (isAuthenticated){
                                    firebaseAuth = Firebase.auth
                                    firebaseAuth.signOut()
                                    Tools.showToast(LocalContext.current, result.error)
                                    finish()
                                }else {
                                    Tools.showToast(LocalContext.current, result.error)
                                    finish()
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
        }
    }


    private fun createUserDetailsOnFirebase(

    ) {
        val cloudFireStore = Firebase.firestore
        val documentReference =
            cloudFireStore.collection(AppConstants.SAFELOCK_USERS).document(userId)
        val userDetails = HashMap<String, Any>()
        userDetails["email"] = loginViewModel.getUserEmail(AppConstants.MAIL).toString()
        userDetails["fingerprintKey"] = UUID.randomUUID().toString()
        userDetails["password"] = loginViewModel.getUserPassword(AppConstants.PASSWORD).toString()
        documentReference.set(userDetails)
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