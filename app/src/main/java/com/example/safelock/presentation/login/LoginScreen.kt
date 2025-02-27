package com.example.safelock.presentation.login

import android.app.Activity
import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.safelock.R
import com.example.safelock.presentation.biometrics.BiometricsActivity
import com.example.safelock.utils.ApiResponse
import com.example.safelock.utils.AppConstants
import com.example.safelock.utils.BiometricPromptManager
import com.example.safelock.utils.CustomLoadingBar
import com.example.safelock.utils.Route
import com.example.safelock.utils.dialog.BiometricsAuthenticationDialog
import com.example.safelock.utils.dialog.ValidationFailureDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


@Composable
fun LoginScreen(
    navController: NavController?,
    viewModel: LoginViewModel = hiltViewModel(),
    isFromBiometrics: Boolean = false
) {
    val context = LocalContext.current
    val biometricsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val isSuccess = result.data?.getBooleanExtra("AUTH_SUCCESS", false) ?: false
                if (isSuccess) {
                    navController?.navigate(Route.DASHBOARD)
                }
            }
        }
    )
    val modifier: Modifier = Modifier
    var nav: NavController
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsState()
    // State to control dialog visibility
    var showValidationFailureDialog by remember { mutableStateOf(false) }
    var showBiometricsDialog by remember { mutableStateOf(true) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showFullContent by remember { mutableStateOf(true) }
    var dialogMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var firebaseAuth: FirebaseAuth
    val cloudFireStore = Firebase.firestore

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(top = 55.dp)
            .background(Color(0xFFF5F7FB))
    ) {
        firebaseAuth = Firebase.auth
        Column(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Hi there!",
                style = MaterialTheme.typography.displayLarge,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Welcome to safe city",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(80.dp))


            if (isLoading) {
                CustomLoadingBar(
                    "Please Wait....",
                    imageResId = R.drawable.loading
                )
                viewModel.clearLoadingState()
            }

            if (showFullContent) {

                //Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(30.dp))

                //Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.password_lock),
                            contentDescription = "Password Icon",
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(30.dp)
                        )
                    },
                    trailingIcon = {
                        val image =
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                )

                Spacer(Modifier.height(88.dp))


                // ValidationFailureDialog
                if (showValidationFailureDialog) {
                    ValidationFailureDialog(
                        title = "Login Failed",
                        message = dialogMessage,
                        onCancelClick = {
                            // Dismiss the dialog
                            showValidationFailureDialog = false
                            viewModel.clearFailureState()
                        }
                    )
                }

                Button(
                    onClick = {
                        viewModel.login(email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Login"
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Dont have an account?", color = Color.Gray)
                    TextButton(onClick = { navController?.navigate("Sign up") }) {
                        Text("Sign up", color = Color.Blue)
                    }

                }

                val currentUser = firebaseAuth.currentUser

                when (val state = loginState) {
                    is ApiResponse.Idle -> {
                        // Do nothing; no feedback to the user yet
                    }

                    is ApiResponse.Loading -> {
                        isLoading = true
                    }

                    is ApiResponse.Success -> {
                        isLoading = false
                        viewModel.saveUserEmail(AppConstants.MAIL, email)
                        viewModel.saveUserPassword(AppConstants.PASSWORD, password)
                        // Launch BiometricsActivity
//                        val intent = Intent(context, BiometricsActivity::class.java)
//                        intent.putExtra(AppConstants.USER_UID, currentUser?.uid)
//                        context.startActivity(intent)
                        val intent = Intent(context, BiometricsActivity::class.java).apply {
                            putExtra(AppConstants.USER_UID, currentUser?.uid)
                        }
                        biometricsLauncher.launch(intent)
                    }

                    is ApiResponse.Failure -> {
                        isLoading = false
                        if (!showValidationFailureDialog) {
                            dialogMessage = state.e?.message ?: AppConstants.GENERIC_ERROR_MSG
                            showValidationFailureDialog = true
                        }
                    }
                }

                val context = AppCompatActivity()
                val promptManager by lazy {
                    BiometricPromptManager(context)
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

                LaunchedEffect(biometricResult, isFromBiometrics) {
                    if (!isFromBiometrics && biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
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

                biometricResult?.let { result ->
                    // Handle side effects using LaunchedEffect
                    if (result is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
                        LaunchedEffect(Unit) {
                            val documentReference =
                                cloudFireStore.collection(AppConstants.SAFELOCK_USERS).document(
                                    currentUser?.uid.toString()
                                )
                            documentReference.get().addOnSuccessListener {
//                                if (it.get("fingerprintKey") == result.toString()
//                                    && currentUser != null
//                                ) {
//                                    navController?.navigate(Route.DASHBOARD)
//                                }
                                navController?.navigate(Route.DASHBOARD){
                                    popUpTo(Route.LOGIN){inclusive = true}
                                }
                            }
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


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(modifier: Modifier = Modifier) {
    LoginScreen(navController = null)
}