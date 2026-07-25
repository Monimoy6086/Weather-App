package com.example.weatherapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double,
    val cityName: String = ""
)

class LocationHelper(private val context: Context) {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val geocoder = Geocoder(context, Locale.getDefault())

    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    fun hasLocationPermission(): Boolean {
        return REQUIRED_PERMISSIONS.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationCoordinates {
        if (!hasLocationPermission()) {
            // Default fallback coordinates (e.g., Lucknow: 26.85, 80.95)
            return getDefaultLocation()
        }
        return suspendCancellableCoroutine { continuation ->
            try {
                val gpsLocation =
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    } else null
                val networkLocation =
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    } else null
                val bestLocation: Location? = gpsLocation ?: networkLocation
                if (bestLocation != null) {
                    continuation.resume(
                        LocationCoordinates(
                            latitude = bestLocation.latitude,
                            longitude = bestLocation.longitude
                        )
                    )
                } else {
                    continuation.resume(getDefaultLocation())
                }
            } catch (e: Exception) {
                continuation.resume(getDefaultLocation())
            }
        }
    }

    suspend fun getCoordinatesFromCityName(cityName: String): LocationCoordinates? = withContext(
        Dispatchers.IO
    ) {
        // Check if a Geocoder service implementation exists on the device
        if (cityName.isBlank() || !Geocoder.isPresent()) {
            return@withContext null
        }

        // Android 13 (API 33) and above uses an asynchronous callback listener
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocationName(cityName, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            val address = addresses.firstOrNull()
                            if (address != null) {
                                continuation.resume(
                                    LocationCoordinates(
                                        latitude = address.latitude,
                                        longitude = address.longitude,
                                        cityName = address.locality ?: cityName
                                    )
                                )
                            } else {
                                continuation.resume(null)
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            continuation.resume(null)
                        }
                    })
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(cityName, 1)
                val address = addresses?.firstOrNull()
                if (address != null) {
                    LocationCoordinates(
                        latitude = address.latitude,
                        longitude = address.longitude,
                        cityName = address.locality ?: cityName
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCityNameFromCoordinates(lat: Double, lon: Double): String? =
        withContext(Dispatchers.IO) {
            if (!Geocoder.isPresent()) return@withContext null
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocation(lat, lon, 1, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                val address = addresses.firstOrNull()
                                continuation.resume(
                                    formatAddress(address)
                                )
                            }

                            override fun onError(errorMessage: String?) {
                                continuation.resume(null)
                            }
                        })
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    val address = addresses?.firstOrNull()
                    formatAddress(address)
                }
            } catch (e: Exception) {
                null
            }
        }

    private fun formatAddress(address: Address?): String? {
        if (address == null) return null
        val city = address.locality
        val state = address.adminArea
        val country = address.countryName
        val parts = listOfNotNull(city, state, country)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
        return if (parts.isNotEmpty()) parts.joinToString(", ") else null
    }

    fun getDefaultLocation(): LocationCoordinates {
        return LocationCoordinates(
            latitude = 26.85,
            longitude = 80.95,
            cityName = "Lucknow"
        )
    }
}