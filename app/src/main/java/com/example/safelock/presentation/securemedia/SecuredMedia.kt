package com.example.safelock.presentation.securemedia

import android.net.Uri
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
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    baseViewModel: BaseViewModel = hiltViewModel(),
    securedMediaViewModel: SecuredMediaViewModel = hiltViewModel()
) {
    val savedImages by baseViewModel.savedImagesInDB.collectAsState()
    var isLoadingForMediaFiles by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val deleteMediaDataState by securedMediaViewModel.deleteMediaDataState.collectAsState()

    baseViewModel.initPromptManager(activity)
    baseViewModel.onScreenViewed("SecuredMedia")
    baseViewModel.appNavigationController = navController

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
        Box(
            modifier = Modifier
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
                val biometricResult by baseViewModel.getPromptManager().promptResults.collectAsState(
                    initial = null
                )
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
                                    val encodedVideoUrl = Uri.encode(mediaItem.imageUrl, "/:?&=")
                                    val testVideoUrl =
                                        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

                                    UploadedItemView(
                                        title = mediaItem.imageTitle,
                                        imageUrl = mediaItem.imageUrl,
                                        isVideo = mediaItem.isVideo,
                                        onVideoClick = {
                                            navController.navigate("video_player_screen?${encodedVideoUrl}")
                                        },
                                        onDeleteButtonClick = {
                                            securedMediaViewModel.deleteMediaByTitle(mediaItem.imageTitle)
                                        },
                                        baseViewModel,
                                        navController
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

                if (isLoading) {
                    Box(
                        modifier
                            .fillMaxSize()
                            .align(Alignment.CenterHorizontally)
                    ){
                        CircularProgressIndicator()
                    }
                }
                LaunchedEffect(deleteMediaDataState) {
                    if (deleteMediaDataState is ApiResponse.Success) {
                        baseViewModel.getSavedImages()
                    }
                }


                when (val state = deleteMediaDataState) {
                    is ApiResponse.Idle -> {}
                    is ApiResponse.Loading -> {
                        isLoading = true
                    }

                    is ApiResponse.Success -> {
                        isLoading = false
                        isLoadingForMediaFiles = false
//                        baseViewModel.getSavedImages()
                    }

                    is ApiResponse.Failure -> {
                        isLoading = false
                        Tools.showToast(LocalContext.current, "Failed to delete Data")
                    }
                }
            }
        }
    }
}


@Composable
fun BiometricResultScreen(
    biometricResult: NewBiometricPromptManagerClass.BiometricResult?,
    navController: NavController
) {
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
                "Authenticated"
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
                    navController.navigate(Route.DASHBOARD) {
                        popUpTo(Route.DASHBOARD) { inclusive = true }
                    }
                }

                NewBiometricPromptManagerClass.BiometricResult.AuthenticationFailed -> {
                    // Possibly navigate or close the screen
                }

                NewBiometricPromptManagerClass.BiometricResult.AuthenticationSuccess -> {
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
    isVideo: Boolean,
    onVideoClick: (String) -> Unit,
    onDeleteButtonClick: () -> Unit,
    baseViewModel: BaseViewModel,
    navController: NavController
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp) // Increased item height
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                if (isVideo) {
                    IconButton(
                        onClick = { onVideoClick(imageUrl) },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.iconsvideo),
                            contentDescription = "Play Video",
                            tint = Color.Red,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                // Delete icon at the bottom right corner
                IconButton(
                    onClick = onDeleteButtonClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            // Title below the image
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
    }
}





