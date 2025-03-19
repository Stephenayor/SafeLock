package com.example.safelock.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.safelock.R
import com.example.safelock.utils.ApiResponse
import com.example.safelock.utils.Route
import com.example.safelock.utils.Tools
import com.example.safelock.utils.base.BaseViewModel
import com.example.safelock.utils.biometrics.NewBiometricPromptManagerClass
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuredMedia(
    modifier: Modifier = Modifier,
    navController: NavController,
    activity: AppCompatActivity,
    baseViewModel: BaseViewModel = hiltViewModel()
) {


    val savedImages by baseViewModel.savedImagesInDB.collectAsState()
    var isLoadingForMediaFiles by remember { mutableStateOf(false) }

    baseViewModel.initPromptManager(activity)
    baseViewModel.onScreenViewed("SecuredMedia")

    Scaffold(
        topBar = {
            // Top App Bar with back button
            androidx.compose.material3.TopAppBar(
                title = { Text(text = "Secured Media") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(
                        onClick = {
                            navController.navigate(Route.DASHBOARD) {
                                popUpTo(Route.DASHBOARD) { inclusive = true }
                            }
                        }
                    ) {
                        Image(
                            painter = painterResource(R.drawable.back_button),
                            contentDescription = "Back",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            )
        },
        modifier = Modifier.padding(top = 10.dp)
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {

                LaunchedEffect(Unit) {
                    baseViewModel.getSavedImages()
                    delay(1000)
                    baseViewModel.showBiometricPrompt(
                        title = "Secure Access",
                        description = "Please authenticate to access your secured media"
                    )
                }

                // Observe the biometric results
                val biometricResult by baseViewModel.getPromptManager().promptResults.collectAsState(initial = null)
                BiometricResultScreen(biometricResult, navController)

                when (val state = savedImages) {
                    is ApiResponse.Idle -> {}
                    is ApiResponse.Loading -> {
                        isLoadingForMediaFiles = true
                    }
                    is ApiResponse.Success -> {
                        isLoadingForMediaFiles = false
                        val savedImagesList = state.data
                        if (savedImagesList != null) {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 130.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(savedImagesList) { mediaItem ->
                                    UploadedItemView(
                                        title = mediaItem.imageTitle,
                                        imageUrl = mediaItem.imageUrl,
                                        baseViewModel
                                    )
                                }
                            }
                        }
                    }
                    is ApiResponse.Failure -> {
                        isLoadingForMediaFiles = false
                        Text(
                            text = "Failed to load media files",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxSize(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}





@Composable
fun BiometricResultScreen(biometricResult: NewBiometricPromptManagerClass.BiometricResult?, navController: NavController) {
    val context = LocalContext.current

    biometricResult?.let { result ->
        // We store the display text
        val message = when (result) {
            is NewBiometricPromptManagerClass.BiometricResult.AuthenticationError -> {
                result.error
            }
            NewBiometricPromptManagerClass.BiometricResult.AuthenticationFailed -> {
                "Authentication Failed"
            }
            NewBiometricPromptManagerClass.BiometricResult.AuthenticationNotSet -> {
                "Authentication Not Set"
            }
            NewBiometricPromptManagerClass.BiometricResult.AuthenticationSuccess -> {
                ""
            }
            NewBiometricPromptManagerClass.BiometricResult.FeatureUnavailable -> {
                "Feature Unavailable"
            }
            NewBiometricPromptManagerClass.BiometricResult.HardwareUnavailable -> {
                "Hardware Unavailable"
            }
        }

        // If authentication failed, finish the activity as a side effect
        if (result == NewBiometricPromptManagerClass.BiometricResult.AuthenticationFailed) {
            LaunchedEffect(Unit) {

            }
        }

        LaunchedEffect(result) {
            when (result) {
                is NewBiometricPromptManagerClass.BiometricResult.AuthenticationError -> {
                    // Navigate to some screen (e.g., a dashboard or error screen)
                    navController.navigate(Route.DASHBOARD) {
                        popUpTo(Route.DASHBOARD) { inclusive = true }
                    }
                }
                NewBiometricPromptManagerClass.BiometricResult.AuthenticationFailed -> {
                    // Possibly navigate or close the screen
                }
                else -> {
                    // No action needed for other states
                }
            }
        }


        Tools.showToast(context, message)
    }
}



@Composable
fun UploadedItemView(
    title: String,
    imageUrl: String,
    baseViewModel: BaseViewModel
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        // Slightly opaque background if needed; you can also use a different color
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp) // Increased item height
            .padding(16.dp)
    ) {
        val context = LocalContext.current
        Column(modifier = Modifier.fillMaxSize()) {

            // Image + Icons
            Box(
                modifier = Modifier
                    .weight(1f) // Takes remaining space
                    .fillMaxWidth()
            ) {
                // Background image
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                }

            // Title below the image
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
    }
}




