@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.safelock.presentation.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.safelock.utils.Tools
import com.example.safelock.utils.Tools.Companion.getCoordinatesFromAddress
import com.example.safelock.utils.base.BaseViewModel
import com.example.safelock.utils.getAddressFromLocation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission", "UnrememberedMutableState")
@Composable
fun LocationComposable() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val userLocation = remember { mutableStateOf<LatLng?>(null) }
    var markerPosition by remember { mutableStateOf(LatLng(37.7749, -122.4194)) }
    var searchQuery by remember { mutableStateOf("") }
    var animateToNewLocation by remember { mutableStateOf(false) }

    val baseViewModel: BaseViewModel = hiltViewModel()
    baseViewModel.onScreenViewed("Location")

    // Request location permission on first composition.
    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    // If permission is granted, fetch the device's last known location.
    if (locationPermissionState.status.isGranted) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        LaunchedEffect(Unit) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@LaunchedEffect
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    userLocation.value = latLng
                    markerPosition = latLng
                    // Geocode the coordinates to an address.
                    val address = Tools.getAddressFromLocation(context, latLng)
                    searchQuery = address ?: "Lat: ${it.latitude}, Lng: ${it.longitude}"
                }
            }
        }
    }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 10f)
    }

    // we are using SnapshotFlow to watch for camera movement.
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.position }
            .distinctUntilChanged()
            .collect { camPos ->
                // When the camera stops moving, update the marker position.
                markerPosition = camPos.target
                // Also update the search bar with the geocoded address.
                val address = getAddressFromLocation(context, markerPosition)
                if (address != null) {
                    searchQuery = address
                }
            }
    }

    // When the user hits Done in the text field, trigger a camera animation.
    LaunchedEffect(animateToNewLocation) {
        if (animateToNewLocation) {
            val newCoordinates = getCoordinatesFromAddress(context, searchQuery)
            newCoordinates?.let { latLng ->
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                )
            }
            animateToNewLocation = false
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            ),
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionState.status.isGranted
            ),
            onMapClick = { latLng ->
                markerPosition = latLng
                val address = Tools.getAddressFromLocation(context, latLng)
                searchQuery = address ?: "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
            }
        ) {
            Marker(
                state = MarkerState(position = markerPosition),
                title = "Selected Location"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Color.White.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Selected Address",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newValue ->
                    searchQuery = newValue
                    // Launch a coroutine to update the marker position as the user types.
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        // Get new coordinates from the entered address.
                        val newCoordinates = getCoordinatesFromAddress(context, newValue)
                        newCoordinates?.let { latLng ->
                            markerPosition = latLng
                            // Animate the camera to the new coordinates.
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                            )
                        }
                    }
                },
                placeholder = {
                    Text(
                        text = "Where to?",
                        color = Color(0xFF9D9EA1),
                        fontFamily = FontFamily.SansSerif
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus() // Hide keyboard
                    }
                ),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search Icon")
                }
            )

        }
    }
}
