package com.example.safelock.presentation.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.example.safelock.R
import com.example.safelock.data.repository.model.DrawerFeature
import com.example.safelock.data.repository.model.MediaData
import com.example.safelock.presentation.analytics.ScreenUsageViewModel
import com.example.safelock.presentation.securemedia.SecureMediaActivity
import com.example.safelock.ui.theme.ThemeViewModel
import com.example.safelock.utils.ApiResponse
import com.example.safelock.utils.CustomLoadingBar
import com.example.safelock.utils.Firebase.EventTracker
import com.example.safelock.utils.Firebase.FirebaseAnalyticsEntryPoint
import com.example.safelock.utils.Route
import com.example.safelock.utils.Tools
import com.example.safelock.utils.Tools.Companion.mapToDrawerFeatures
import com.example.safelock.utils.base.BaseViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.EntryPointAccessors
import `in`.mayanknagwanshi.imagepicker.ImageSelectActivity
import kotlinx.coroutines.launch
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardScreen(
    modifier: Modifier = Modifier,
    navController: NavController?,
    themeViewModel: ThemeViewModel?,
    viewModel: DashBoardViewModel = hiltViewModel(),
    screenUsageViewModel: ScreenUsageViewModel = hiltViewModel(),
    baseViewModel: BaseViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val mostUsedScreens by baseViewModel.mostUsedScreens.collectAsState()
    val drawerFeatures = mapToDrawerFeatures(mostUsedScreens)
//    val analytics = analyticsViewModel.analytics
    val analytics = baseViewModel.analytics
    val uploadImageOrVideoUriCloud by viewModel.uploadImageVideo.collectAsState()
    val uploadMediaDataFireStoreDatabase by viewModel.uploadMediaDataStateFireStore.collectAsState()
    val getAllMediaFiles by viewModel.getAllMediaItems.collectAsState()
    var fileUriOnFirebaseCloudStorage: Uri?
    var mediaTitle: String? = ""
    var isLoading by remember { mutableStateOf(false) }
    var isCloudUpload by remember { mutableStateOf(false) }
    var isLoadingForMediaFiles by remember { mutableStateOf(false) }
    var mediaUri: Uri? = null
    val firebaseAuth = hiltViewModel<DashBoardViewModel>().firebaseAuth

    // Launcher for Image Result
    val pair = imagePickerLauncher(mediaTitle, viewModel, mediaUri)
    val imagePickerLauncher = pair.first
    mediaUri = pair.second

    //Launcher for Video
    val pickVideoIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "video/*"
    }
    val recordVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)


    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val videoUri: Uri? = data?.data
            if (videoUri != null) {
                // Proceed to upload
                videoUri.path?.let {
                    val videoName = File(it).name
                    viewModel.setMediaTitle(videoName)
                }
                uploadImageOrVideoToCloudStorage(videoUri, viewModel, videoUri.path, true)
            } else {
                Log.d("Video selection", "Failed to get video URI")
            }
        }
    }

    val firebaseAnalytics = remember {
        EntryPointAccessors.fromActivity(
            context as Activity,
            FirebaseAnalyticsEntryPoint::class.java
        ).getFirebaseAnalytics()
    }
    trackScreen("DashBoardGuy", "DashBoardScreen", baseViewModel)


    // Fetch data when screen appears
    LaunchedEffect(Unit) {
        baseViewModel.fetchMostUsedScreens()
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "DASHBOARD_SCREEN_VIEW")
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "DashBoardScreen")
        }
        analytics.logEvent("dashboard_view", bundle)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        modifier = Modifier.fillMaxSize(),
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Most Used Features",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )


                if (drawerFeatures.isNotEmpty()) {
                    drawerFeatures.forEach { feature ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable {
                                    if (feature.screenName == "DashBoard") {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }
                                    if (feature.screenName == "SecuredMedia") {
                                        SecureMediaActivity.start(context)
                                    }
                                    if (feature.screenName == "Location") {
                                        navController?.navigate(Route.LOCATION)
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = feature.screenName
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = feature.screenName)
                        }
                    }
                } else {
                    val drawerFeature = listOf(
                        DrawerFeature("DashBoard", Icons.Default.Home),
                        DrawerFeature("Secured Media", Icons.Default.Person),
                        DrawerFeature("Location", Icons.Default.LocationOn)
                    )
                    drawerFeature.forEach { feature ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable {
                                    // handle navigation or action
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = feature.screenName
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = feature.screenName)
                        }
                    }
                }

                // Spacer to push logout to the bottom
                Spacer(modifier = Modifier.weight(1f))

                //Dark/Light Theme Toggle
                val darkThemeEnabled by themeViewModel?.theme!!.collectAsState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { themeViewModel?.setTheme(!darkThemeEnabled) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Change App Theme",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = darkThemeEnabled,
                        onCheckedChange = { themeViewModel?.setTheme(it) }
                    )
                }


                // Logout row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            firebaseAuth.signOut()
                            navController?.navigate(Route.SPLASH_SCREEN) {
                                popUpTo("Splash screen") { inclusive = true }
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logout",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        FloatingActionButton(
                            onClick = {

                            },
                            contentColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            TextButton(onClick = {
                                // Create a chooser combining both
                                val chooser = Intent.createChooser(
                                    pickVideoIntent,
                                    "Select or Record a Video"
                                )
                                chooser.putExtra(
                                    Intent.EXTRA_INITIAL_INTENTS,
                                    arrayOf(recordVideoIntent)
                                )
                                // Launch the chooser
                                videoPickerLauncher.launch(chooser)

                            }) {
                                Icon(
                                    imageVector = Icons.Filled.AddCard,
                                    contentDescription = "Add Video",
                                    tint = Color.White
                                )
                                Text("Add Video", color = Color.White)
                            }
                        }
                    },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                )
                baseViewModel.onScreenViewed("DashBoard")
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val bundle = EventTracker.trackEvent("DASHBOARD SCREEN", "DashBoardScreen")
                        analytics.logEvent("floating_action_button_click", bundle)
                        val intent = Intent(context, ImageSelectActivity::class.java).apply {
                            putExtra(ImageSelectActivity.FLAG_COMPRESS, false)
                            putExtra(ImageSelectActivity.FLAG_CAMERA, true)
                            putExtra(ImageSelectActivity.FLAG_GALLERY, true)
                            putExtra(ImageSelectActivity.FLAG_CROP, false)
                        }
                        imagePickerLauncher.launch(intent)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surfaceBright
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add"
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->
            Box(
                modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(paddingValues)
                    .padding(top = 5.dp)
            ) {

                when (val state = getAllMediaFiles) {
                    is ApiResponse.Idle -> {
                        // Do nothing; no feedback to the user yet
                    }

                    is ApiResponse.Loading -> {
                        isLoadingForMediaFiles = true
                    }

                    is ApiResponse.Success -> {
                        isLoadingForMediaFiles = false
                        val mediaData = state.data
                        if (mediaData != null) {
                            MediaItemGridView(mediaData, mediaUri, baseViewModel, navController!!)
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (mediaData?.isEmpty() == true) {
                                Text(
                                    "Upload your items here",
                                    style = TextStyle(
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 20.sp
                                    ),
                                    color = Color.Blue
                                )
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
            if (isLoading) {
                CustomLoadingBar(
                    "Uploading to Secured Space",
                    imageResId = R.drawable.loading
                )
                viewModel.clearLoadingState()
            }

            if (isLoadingForMediaFiles) {
                CustomLoadingBar(
                    "Getting valuables",
                    imageResId = R.drawable.loading
                )
            }

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uploadImageOrVideoUriCloud) {
            is ApiResponse.Idle -> {
                // Do nothing; no feedback to the user yet
            }

            is ApiResponse.Loading -> {
                CircularProgressIndicator()
            }

            is ApiResponse.Success -> {
                isLoading = false
                Tools.showToast(context, "Cloud Upload Successful")
                fileUriOnFirebaseCloudStorage = state.data
                isCloudUpload = true
                viewModel.uploadMediaDataToFireStoreDatabase(
                    fileUriOnFirebaseCloudStorage!!,
                    ""
                )
                viewModel.clearSuccessState()
            }

            is ApiResponse.Failure -> {
                isLoading = false
            }
        }
    }


    when (val state = uploadMediaDataFireStoreDatabase) {
        is ApiResponse.Idle -> {
            // Do nothing; no feedback to the user yet
        }

        is ApiResponse.Loading -> {
            isLoading = true
        }

        is ApiResponse.Success -> {
            isLoading = false
            viewModel.getAllMediaItems
        }

        is ApiResponse.Failure -> {
            isLoading = false
        }
    }

}


@Composable
private fun imagePickerLauncher(
    mediaTitle: String?,
    viewModel: DashBoardViewModel,
    mediaUri: Uri?
): Pair<ManagedActivityResultLauncher<Intent, ActivityResult>, Uri?> {
    var mediaUri1 = mediaUri
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val filePath = data?.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH)
            filePath?.let {
                val fileName = File(it).name
                viewModel.setMediaTitle(fileName)
            }
            val selectedImage = BitmapFactory.decodeFile(filePath)
            val fileUri = Uri.fromFile(filePath?.let { File(it) })
            mediaUri1 = Uri.fromFile(filePath?.let { File(it) })
            uploadImageOrVideoToCloudStorage(fileUri, viewModel, filePath, false)
        } else {
            Log.d("Image selection Failed", "Failed to select image")
        }
    }
    return Pair(imagePickerLauncher, mediaUri1)
}


fun uploadImageOrVideoToCloudStorage(
    fileUri: Uri?,
    viewModel: DashBoardViewModel,
    filePath: String?,
    isVideo: Boolean

) {
    Log.d("cloud", "here")

    if (!isVideo) {
        fileUri?.let { viewModel.uploadImageToCloud(it) }
    } else {
        fileUri?.let { viewModel.uploadVideoToCloud(it) }
    }
}

@Composable
fun MediaItemGridView(
    mediaItems: List<MediaData>, mediaUri: Uri?,
    baseViewModel: BaseViewModel, navController: NavController
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 130.dp), // 3 columns for a 3x3 grid
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(mediaItems) { mediaItem ->
            UploadedItemView(
                title = mediaItem.dataTitle.orEmpty(),
                imageUrl = mediaItem.dataImage.orEmpty(),
                playIcon = if (!mediaItem.dataTitle.orEmpty().contains(".jpeg", ignoreCase = true)
                    && !mediaItem.dataTitle.orEmpty().contains(".png", ignoreCase = true)
                ) Icons.Default.PlayArrow else null,
                mediaUri = Uri.parse(mediaItem.dataImage),
                baseViewModel,
                navController
            )
        }
    }
}

@Composable
fun UploadedItemView(
    title: String,
    imageUrl: String,
    playIcon: ImageVector?,
    mediaUri: Uri?,
    baseViewModel: BaseViewModel,
    navController: NavController
) {
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val isSuccess = result.data?.getBooleanExtra("AUTH_SUCCESS", false) ?: false
                if (isSuccess) {
                    navController.navigate(Route.SECURED_MEDIA)
                }
            }
        }
    )

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp) // Increased item height
            .padding(16.dp)
    ) {
        val context = LocalContext.current
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f) // Takes remaining space
                    .fillMaxWidth()
            ) {
                val isVideo = (playIcon != null)

                if (isVideo) {
                    val model = ImageRequest.Builder(context)
                        .data(mediaUri)
                        .videoFrameMillis(10000)
                        .decoderFactory { result, options, _ ->
                            VideoFrameDecoder(
                                result.source,
                                options
                            )
                        }
                        .build()
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = model,
                        contentDescription = "video thumbnail",
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }


                // Save button (top-right)
                Card(
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    IconButton(onClick = {
                        if (isVideo) {
                            baseViewModel.saveImagesInDB(imageUrl, title, true)
                        } else {
                            baseViewModel.saveImagesInDB(imageUrl, title, false)
                        }
                        Tools.showToast(context, "Saved successfully")
                        val intent = Intent(context, SecureMediaActivity::class.java).apply {
                            putExtra("navController", navController.toString())
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Button",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Play button (center), only if playIcon != null
                playIcon?.let { icon ->
                    Card(
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        IconButton(onClick = {
                            Tools.showToast(
                                context, "You need to Save Video, " +
                                        "before you can play"
                            )
                        }) {
                            Icon(
                                Icons.Default.PlayCircle,
                                contentDescription = "Play video icon",
                                modifier = Modifier.size(30.dp),

                                )
                        }
                    }
                }

                // Bluetooth button (bottom-left)
                Card(
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                ) {
                    IconButton(onClick = {
                        mediaUri?.let {
                            shareMediaViaBluetooth(
                                context = context,
                                uri = it,
                                isImage = playIcon == null// or false if it’s a video
                            )
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Bluetooth,
                            contentDescription = "Bluetooth Button",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Share button (bottom-right)
                Card(
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    IconButton(onClick = { /* Handle Share */
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Button",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
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


fun shareMediaViaBluetooth(context: Context, uri: Uri, isImage: Boolean) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = if (isImage) "image/*" else "video/*"
        putExtra(Intent.EXTRA_STREAM, uri)
    }
    val packageManager = context.packageManager
    // Query all activities that can handle the share intent
    val activities = packageManager.queryIntentActivities(shareIntent, 0)
    // Try to find an activity with "bluetooth" in its package name
    val bluetoothActivity = activities.find {
        it.activityInfo.packageName.contains("bluetooth", ignoreCase = true)
    }

    if (bluetoothActivity != null) {
        shareIntent.setPackage(bluetoothActivity.activityInfo.packageName)
        try {
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Error sharing via Bluetooth", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(
            context,
            "Bluetooth sharing is not available on this device",
            Toast.LENGTH_SHORT
        ).show()
    }
}


@Preview(showBackground = true)
@Composable
fun DashBoardScreenPreview(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 16.dp)
    ) {
        val navController = rememberNavController()

        DashBoardScreen(modifier, navController, null)
    }
}


@SuppressLint("ComposableNaming")
@Composable
fun trackScreen(
    screenName: String, screenClass: String,
    baseViewModel: BaseViewModel
) {
    DisposableEffect(Unit) {
        onDispose {
            val bundle = EventTracker.trackEvent(screenName, screenClass)
            baseViewModel.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }
}