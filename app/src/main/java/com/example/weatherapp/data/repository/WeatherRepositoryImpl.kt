package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.DailyForecastEntity
import com.example.weatherapp.data.local.entities.HourlyForecastEntity
import com.example.weatherapp.data.local.entities.WeatherAlertEntity
import com.example.weatherapp.data.remote.WeatherApiService
import com.example.weatherapp.domain.model.CurrentWeather
import com.example.weatherapp.domain.model.DailyForecast
import com.example.weatherapp.domain.model.FullWeatherData
import com.example.weatherapp.domain.model.HourlyForecast
import com.example.weatherapp.domain.model.Resource
import com.example.weatherapp.domain.model.SeverityLevel
import com.example.weatherapp.domain.model.WeatherAlert
import com.example.weatherapp.domain.model.WeatherCondition
import com.example.weatherapp.utils.LocationHelper
import com.example.weatherapp.utils.NetworkUtils
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

class WeatherRepositoryImpl(
    private val weatherDao: WeatherDao,
    private val apiService: WeatherApiService,
    private val locationHelper: LocationHelper,
    private val context: Context
) : WeatherRepository {

    // 30 minutes cache TTL
    private val CACHE_EXPIRATION_MS = TimeUnit.MINUTES.toMillis(30)

    override fun getWeatherForecastStream(
        lat: Double,
        lon: Double,
        forceRefresh: Boolean
    ): Flow<Resource<FullWeatherData>> = flow {
        emit(Resource.Loading)

        // Combine DB flows into domain object
        val cityName = locationHelper.getCityNameFromCoordinates(lat, lon)
        val dbFlow = cityName?.let {
            combine(
                weatherDao.getCurrentWeather(it),
                weatherDao.getHourlyForecasts(it),
                weatherDao.getDailyForecasts(it),
                weatherDao.getWeatherAlerts(it)
            ) { currentEntity, hourlyEntities, dailyEntities, alertEntities ->
                if (currentEntity == null) null
                else mapToDomain(currentEntity, hourlyEntities, dailyEntities, alertEntities)
            }
        }

        var hasFetched = false
        dbFlow?.collect { cachedData ->
            val now = System.currentTimeMillis()
            val isInternetAvailable = NetworkUtils.isInternetAvailable(context)
            val isCacheStale =
                cachedData == null || (now - cachedData.lastRefreshedAt) > CACHE_EXPIRATION_MS

            if (cachedData != null) {
                // Emit current cache immediately
                emit(Resource.Success(data = cachedData, isOffline = !isInternetAvailable))
            }

            if (!hasFetched && (isCacheStale || forceRefresh)) {
                if (isInternetAvailable) {
                    hasFetched = true
                    try {
                        val networkData = apiService.fetchWeatherData(lat, lon)
                        // Update Room Database atomically
                        saveToCache(networkData)
                    } catch (e: Exception) {
                        if (cachedData != null) {
                            emit(
                                Resource.Success(
                                    data = cachedData.copy(isCached = true),
                                    isOffline = true
                                )
                            )
                        } else {
                            emit(
                                Resource.Error(
                                    message = e.localizedMessage ?: "Failed to fetch weather data"
                                )
                            )
                        }
                    }
                } else if (cachedData == null) {
                    // No internet and no cache
                    emit(Resource.Error(message = "No internet connection and no saved data available"))
                }
            }
        }
    }

    override suspend fun refreshWeatherCache(lat: Double, lon: Double): Result<Unit> {
        return try {
            val freshData = apiService.fetchWeatherData(lat, lon)
            saveToCache(freshData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveToCache(data: FullWeatherData) {
        // Ensure only the current location data is saved in DB
        val currentEntity = CurrentWeatherEntity(
            cityName = data.currentWeather.cityName,
            temperatureC = data.currentWeather.temperatureC,
            feelsLikeC = data.currentWeather.feelsLikeC,
            humidity = data.currentWeather.humidity,
            windSpeedKmh = data.currentWeather.windSpeedKmh,
            uvIndex = data.currentWeather.uvIndex,
            airQualityIndex = data.currentWeather.airQualityIndex,
            conditionName = data.currentWeather.condition.name,
            conditionText = data.currentWeather.conditionText,
            updatedAtMillis = data.lastRefreshedAt
        )

        val hourlyEntities = data.hourlyForecasts.map {
            HourlyForecastEntity(
                cityName = data.currentWeather.cityName,
                timeFormatted = it.timeFormatted,
                timestampMillis = it.timestampMillis,
                temperatureC = it.temperatureC,
                precipitationChance = it.precipitationChance,
                conditionName = it.condition.name
            )
        }

        val dailyEntities = data.dailyForecasts.map {
            DailyForecastEntity(
                cityName = data.currentWeather.cityName,
                dayOfWeek = it.dayOfWeek,
                dateText = it.dateText,
                maxTempC = it.maxTempC,
                minTempC = it.minTempC,
                precipitationChance = it.precipitationChance,
                conditionName = it.condition.name,
                conditionSummary = it.conditionSummary
            )
        }

        val alertEntities = data.activeAlerts.map {
            WeatherAlertEntity(
                alertId = it.id,
                cityName = data.currentWeather.cityName,
                title = it.title,
                description = it.description,
                severity = it.severity.name,
                issueTimeFormatted = it.issueTimeFormatted
            )
        }

        weatherDao.updateFullWeatherCache(
            current = currentEntity,
            hourly = hourlyEntities,
            daily = dailyEntities,
            alerts = alertEntities
        )
    }

    private fun mapToDomain(
        current: CurrentWeatherEntity,
        hourly: List<HourlyForecastEntity>,
        daily: List<DailyForecastEntity>,
        alerts: List<WeatherAlertEntity>
    ): FullWeatherData {
        val alertIds = alerts.map { it.alertId }
        val currentWeather = CurrentWeather(
            cityName = current.cityName,
            temperatureC = current.temperatureC,
            feelsLikeC = current.feelsLikeC,
            humidity = current.humidity,
            windSpeedKmh = current.windSpeedKmh,
            uvIndex = current.uvIndex,
            airQualityIndex = current.airQualityIndex,
            condition = safeEnum<WeatherCondition>(current.conditionName) ?: WeatherCondition.SUNNY,
            conditionText = current.conditionText,
            updatedAtMillis = current.updatedAtMillis,
            alerts = alertIds
        )

        val hourlyList = hourly.map {
            HourlyForecast(
                timeFormatted = it.timeFormatted,
                timestampMillis = it.timestampMillis,
                temperatureC = it.temperatureC,
                precipitationChance = it.precipitationChance,
                condition = safeEnum<WeatherCondition>(it.conditionName) ?: WeatherCondition.SUNNY
            )
        }

        val dailyList = daily.map {
            DailyForecast(
                dayOfWeek = it.dayOfWeek,
                dateText = it.dateText,
                maxTempC = it.maxTempC,
                minTempC = it.minTempC,
                precipitationChance = it.precipitationChance,
                condition = safeEnum<WeatherCondition>(it.conditionName) ?: WeatherCondition.SUNNY,
                conditionSummary = it.conditionSummary
            )
        }

        val alertList = alerts.map {
            WeatherAlert(
                id = it.alertId,
                title = it.title,
                description = it.description,
                severity = safeEnum<SeverityLevel>(it.severity) ?: SeverityLevel.INFO,
                issueTimeFormatted = it.issueTimeFormatted
            )
        }

        return FullWeatherData(
            currentWeather = currentWeather,
            hourlyForecasts = hourlyList,
            dailyForecasts = dailyList,
            activeAlerts = alertList,
            isCached = true,
            lastRefreshedAt = current.updatedAtMillis
        )
    }

    private inline fun <reified T : Enum<T>> safeEnum(name: String): T? {
        return try {
            enumValueOf<T>(name)
        } catch (e: Exception) {
            null
        }
    }
}
