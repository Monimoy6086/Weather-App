package com.example.weatherapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.utils.LocationHelper
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.roundToLong

class WeatherSyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: WeatherRepository,
    private val locationHelper: LocationHelper,
    private val weatherDao: WeatherDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val inputLat = inputData.getDouble(KEY_LAT, DEFAULT_SENTINEL)
        val inputLon = inputData.getDouble(KEY_LON, DEFAULT_SENTINEL)
        val (lat, lon) = if (inputLat != DEFAULT_SENTINEL && inputLon != DEFAULT_SENTINEL) {
            Pair(inputLat, inputLon)
        } else {
            val location = locationHelper.getCurrentLocation()
            Pair(location.latitude, location.longitude)
        }

        val cacheKey = "${(lat * 100).roundToLong() / 100.0}_${(lon * 100).roundToLong() / 100.0}"

        return try {
            val refreshResult = repository.refreshWeatherCache(lat, lon)
            if (refreshResult.isSuccess) {
                // Fetch updated DB state from WeatherDatabase to check for active critical alerts
                val cachedAlerts = weatherDao.getWeatherAlerts(cacheKey).firstOrNull().orEmpty()
                val criticalAlerts = cachedAlerts.filter {
                    it.severity.equals(
                        "CRITICAL",
                        ignoreCase = true
                    ) || it.severity.equals("WARNING", ignoreCase = true)
                }
                if (criticalAlerts.isNotEmpty()) {
                    criticalAlerts.forEachIndexed { index, alert ->
                        NotificationHelper.showWeatherAlertNotification(
                            context = applicationContext,
                            notificationId = (cacheKey.hashCode() + index),
                            title = "⚠️ ${alert.title}",
                            message = alert.description
                        )
                    }
                }

                NotificationHelper.showWeatherAlertNotification(
                    context = applicationContext,
                    notificationId = cacheKey.hashCode(),
                    title = "Weather Sync Completed",
                    message = "Background weather data refreshed for location [$lat, $lon]."
                )

                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_LAT = "key_lat"
        const val KEY_LON = "key_lon"
        const val WORK_NAME = "weather_background_sync_worker"
        private const val DEFAULT_SENTINEL = -999.0
    }
}
