package com.nevaxr.foundation.car.device

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import com.nevaxr.device.LocationState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.time.Duration

@RequiresPermission(anyOf = [ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION])
fun Context.getLocationFlow(interval: Duration): Flow<LocationState> = callbackFlow {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

    val gpsLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val locationState = LocationState(
                longitude = location.longitude,
                latitude = location.latitude,
                bearing = location.bearing,
                altitude = location.altitude,
                accuracy = location.accuracy,
                speed = location.speed,
                timestamp = location.time,
            )
            trySend(locationState) // Emit GPS location
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    val networkLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val locationState = LocationState(
                longitude = location.longitude,
                latitude = location.latitude,
                bearing = location.bearing,
                altitude = location.altitude,
                accuracy = location.accuracy,
                speed = location.speed,
                timestamp = location.time,
            )
            trySend(locationState)
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    if (hasGps) {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            interval.inWholeMilliseconds,
            0F,
            gpsLocationListener
        )
    }

    if (hasNetwork) {
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            interval.inWholeMilliseconds,
            0F,
            networkLocationListener
        )
    }

    awaitClose {
        locationManager.removeUpdates(gpsLocationListener)
        locationManager.removeUpdates(networkLocationListener)
    }
}