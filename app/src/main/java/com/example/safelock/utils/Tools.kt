package com.example.safelock.utils

import android.content.Context
import android.graphics.Bitmap
import android.location.Geocoder
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import com.example.safelock.data.repository.database.entity.ScreenUsageEntity
import com.example.safelock.data.repository.model.DrawerFeature
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class Tools {
    companion object{
        fun showToast(context: Context?, message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun mapToDrawerFeatures(screenUsages: List<ScreenUsageEntity>): List<DrawerFeature> {
            return screenUsages.map { usage ->
                val icon = when (usage.screenName) {
                    "DashBoard" -> Icons.Filled.Home
                    "SecuredMedia" -> Icons.Filled.PermMedia
                    "Profile" -> Icons.Filled.Person
                    else -> Icons.Filled.Star
                }
                DrawerFeature(usage.screenName, icon)
            }
        }

        //Generate Video Thumbnail
        fun getVideoThumbnail(context: Context, videoUri: Uri): Bitmap? {
            val retriever = MediaMetadataRetriever()
            return try {
                retriever.setDataSource(context, videoUri)
                // getFrameAtTime(0) returns the first frame
                retriever.getFrameAtTime(0)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                retriever.release()
            }
        }

         fun getAddressFromLocation(context: Context, latLng: LatLng): String? {
                return try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        addresses[0].getAddressLine(0)
                    } else null
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
        }

       fun getCoordinatesFromAddress(context: Context, address: String): LatLng? {
           return try {
               val geocoder = Geocoder(context, Locale.getDefault())
               val addresses = geocoder.getFromLocationName(address, 1)
               if (!addresses.isNullOrEmpty()) {
                   LatLng(addresses[0].latitude, addresses[0].longitude)
               } else null
           } catch (e: Exception) {
               e.printStackTrace()
               null
           }
       }

    }
}