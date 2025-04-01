package com.example.safelock.utils

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

 suspend fun getAddressFromLocation(context: Context, latLng: LatLng): String? {
    return withContext(Dispatchers.IO) {
        try {
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
}
