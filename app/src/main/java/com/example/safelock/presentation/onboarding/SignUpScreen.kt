package com.example.safelock.presentation.onboarding

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.safelock.R
import com.example.safelock.presentation.biometrics.BiometricsActivity
import com.example.safelock.utils.ApiResponse
import com.example.safelock.utils.AppConstants
import com.example.safelock.utils.CustomLoadingBar
import com.example.safelock.utils.Route
import com.example.safelock.utils.dialog.SuccessDialog
import com.example.safelock.utils.dialog.ValidationFailureDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun SignUpScreen(
    navController: NavController?,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val modifier: Modifier = Modifier
    val signUpState by viewModel.signUpState.collectAsState()
    // State to control dialog visibility
    var showValidationFailureDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val biometricsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val isSuccess = result.data?.getBooleanExtra("AUTH_SUCCESS", false) ?: false
                if (isSuccess) {
                    navController?.navigate(Route.HOME_SCREEN) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                }
            }
        }
    )



    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            if (showSuccessDialog) {
                SuccessDialog(
                    title = AppConstants.ACCOUNT_CREATED,
                    subtitle = "Please proceed to login to secure your valuables",
                    buttonText = "Continue",
                    onButtonClick = {
                        // Navigate to the next screen
                        navController?.navigate("Login")
                        viewModel.clearSuccessState()
                    }
                )
            }

            Image(
                painter = painterResource(id = R.drawable.safelock_signup_img),
                contentDescription = "Placeholder inside the sign up screen",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(top = 45.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CustomLoadingBar(
                    "Please Wait....",
                    imageResId = R.drawable.loading
                )
                viewModel.clearLoadingState()
            }

            Text(
                text = "Sign up",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Unspecified,
                fontSize = 45.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))


            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email address") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = "Email Icon",
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(30.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field with Toggle
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
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
                        Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(25.dp))

            // ValidationFailureDialog
            if (showValidationFailureDialog) {
                ValidationFailureDialog(
                    title = "Sign up unsuccessful",
                    message = dialogMessage,
                    onCancelClick = {
                        // Dismiss the dialog
                        showValidationFailureDialog = false
                        viewModel.clearFailureState()
                    }
                )
            }


            // Terms and Conditions
            Text(
                text = "By signing up, you're agree to our Terms & Conditions\nand Privacy Policy",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Continue Button
            Button(
                onClick = { viewModel.signUp(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Log.d("email", email)
                Log.d("password", password)
                Text("Continue", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Joined us Before?", color = Color.Gray)
                TextButton(onClick = {
                    val currentUser = Firebase.auth.currentUser
                    if (currentUser != null) {
                        val intent = Intent(context, BiometricsActivity::class.java).apply {
                            putExtra(AppConstants.USER_UID, currentUser.uid)
                            putExtra(AppConstants.GETTING_STARTED, true)
                        }
                        biometricsLauncher.launch(intent)
                    } else {
                        navController?.navigate(Route.LOGIN)
                    }
                }) {
                    Text("Login", color = MaterialTheme.colorScheme.primary)
                }

            }


            when (val state = signUpState) {
                is ApiResponse.Idle -> {
                    // Do nothing; no feedback to the user yet
                }

                is ApiResponse.Loading -> {
                    isLoading = true
                }

                is ApiResponse.Success -> {
                    isLoading = false
                    if (!showSuccessDialog) {
                        showSuccessDialog = true
                    }

                }

                is ApiResponse.Failure -> {
                    isLoading = false
                    if (!showValidationFailureDialog) {
                        dialogMessage = state.e?.message ?: "Sign-up Failed!"
                        showValidationFailureDialog = true
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SignUpScreenPreview() {
    SignUpScreen(navController = null)
}