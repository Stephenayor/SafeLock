package com.example.safelock.presentation.dashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.safelock.R
import com.example.safelock.data.repository.model.MediaData
import com.example.safelock.utils.ApiResponse
import com.example.safelock.utils.CustomLoadingBar
import com.example.safelock.utils.Tools
import `in`.mayanknagwanshi.imagepicker.ImageSelectActivity
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashBoardViewModel = hiltViewModel()
) {

    // Trigger data fetching when the screen appears
    LaunchedEffect(Unit) {
        viewModel.getAllMediaItems()
    }
    val context = LocalContext.current
    val uploadImageState by viewModel.uploadImage.collectAsState()
    val uploadMediaDataState by viewModel.uploadMediaDataState.collectAsState()
    val getAllMediaFiles by viewModel.getAllMediaItems.collectAsState()
    var imageUriOnFirebaseCloudStorage: Uri?
    var mediaTitle: String? = ""
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingForMediaFiles by remember { mutableStateOf(false) }
    var mediaUri: Uri? = null

    // Launcher for Activity Result
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            // Handle the selected image URI
            val filePath = data?.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH)
            mediaTitle = filePath.let {
                File(it.toString()).name
            }
            filePath?.let {
                val fileName = File(it).name
                viewModel.setMediaTitle(fileName)
            }
            val selectedImage = BitmapFactory.decodeFile(filePath)
            val fileUri = Uri.fromFile(filePath?.let { File(it) })
            mediaUri =  Uri.fromFile(filePath?.let { File(it) })
            Log.d("URI", fileUri.toString())
            uploadImageToCloud(fileUri, viewModel, filePath)
        } else {
            Log.d("Image selection Failed", "Failed to select image")
        }
    }

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
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu"
                        )
                    }
                }

            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, ImageSelectActivity::class.java).apply {
                        putExtra(ImageSelectActivity.FLAG_COMPRESS, false)
                        putExtra(ImageSelectActivity.FLAG_CAMERA, true)
                        putExtra(ImageSelectActivity.FLAG_GALLERY, true)
                        putExtra(ImageSelectActivity.FLAG_CROP, false)
                    }
                    imagePickerLauncher.launch(intent)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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
                .padding(top = 16.dp)
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
                        MediaItemGridView(mediaData, mediaUri)
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
                "Uploading Data to Secured Space",
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

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center // Centers content within the Box
    ) {
        when (val state = uploadImageState) {
            is ApiResponse.Idle -> {
                // Do nothing; no feedback to the user yet
            }

            is ApiResponse.Loading -> {
                CircularProgressIndicator()
            }

            is ApiResponse.Success -> {
                isLoading = false
                Tools.showToast(context, "Image Upload Successful")
                imageUriOnFirebaseCloudStorage = state.data
                    viewModel.uploadMediaDataToFireStoreDatabase(
                        imageUriOnFirebaseCloudStorage!!,
                        ""
                    )
            }

            is ApiResponse.Failure -> {
                isLoading = false
            }
        }
    }


    when (val state = uploadMediaDataState) {
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


fun uploadImageToCloud(
    fileUri: Uri?,
    viewModel: DashBoardViewModel,
    filePath: String?
) {
    if (fileUri.toString().contains(".png", ignoreCase = true) ||
        fileUri.toString().contains(".jpeg", ignoreCase = true)
        ){
        fileUri?.let { viewModel.uploadImageToCloud(it) }
    }else{
        fileUri?.let { viewModel.uploadVideoToCloud(it) }
    }

//    fileUri?.let { viewModel.uploadImageToCloud(it) }
}

@Composable
fun MediaItemGridView(mediaItems: List<MediaData>, mediaUri: Uri?) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 130.dp), // 3 columns for a 3x3 grid
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
        ,
        contentPadding = PaddingValues(8.dp)
    ) {
        items(mediaItems) { mediaItem ->
            UploadedItemView(
                title = mediaItem.dataTitle.orEmpty(),
                imageUrl = mediaItem.dataImage.orEmpty(),
                playIcon = if (!mediaItem.dataTitle.orEmpty().contains(".jpeg", ignoreCase = true)
                    && !mediaItem.dataTitle.orEmpty().contains(".png", ignoreCase = true)
                ) Icons.Default.PlayArrow else null,
                mediaUri = Uri.parse(mediaItem.dataImage)
            )
        }
    }
}

@Composable
fun UploadedItemView(
    title: String,
    imageUrl: String,
    playIcon: ImageVector?,
    mediaUri: Uri?
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

                // Save button (top-right)
                Card(
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    IconButton(onClick = { /* Handle save */ }) {
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
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        IconButton(onClick = { /* Handle play */ }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Play Button",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
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
        Toast.makeText(context, "Bluetooth sharing is not available on this device", Toast.LENGTH_SHORT).show()
    }
}



@Composable
private fun PickImageFromGallery() {

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

        DashBoardScreen(modifier)
    }
}