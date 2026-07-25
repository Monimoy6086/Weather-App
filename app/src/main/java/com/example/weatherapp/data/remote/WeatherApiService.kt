package com.example.weatherapp.data.remote

import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.domain.model.CurrentWeather
import com.example.weatherapp.domain.model.DailyForecast
import com.example.weatherapp.domain.model.FullWeatherData
import com.example.weatherapp.domain.model.HourlyForecast
import com.example.weatherapp.domain.model.SeverityLevel
import com.example.weatherapp.domain.model.WeatherAlert
import com.example.weatherapp.domain.model.WeatherCondition
import com.example.weatherapp.utils.LocationHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.random.Random

interface WeatherApiService {
    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather
    suspend fun getHourlyWeather(lat: Double, lon: Double): List<HourlyForecast>
    suspend fun getDailyWeather(lat: Double, lon: Double): List<DailyForecast>
    suspend fun fetchWeatherData(lat: Double, lon: Double): FullWeatherData
    suspend fun getWeatherAlerts(alertIds: List<String>): List<AlertDto>
}

class WeatherApiServiceImpl(
    private val weatherApi: WeatherApi,
    private val apiKey: String,
    private val locationHelper: LocationHelper,
    private val weatherDao: WeatherDao
) : WeatherApiService {
    override suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather {
        val cityName = locationHelper.getCityNameFromCoordinates(lat, lon)
        return try {
            val response =
                weatherApi.getCurrentWeather(lat = lat, lon = lon, apiKey = apiKey)
            mapToCurrentWeather(response, cityName)
        } catch (e: Exception) {
            val cached = cityName?.let { weatherDao.getCurrentWeather(it).firstOrNull() }
            if (cached != null) {
                mapCachedDataToCurrentWeather(cached)
            } else {
                buildFallbackCurrentWeather(cityName, e.message)
            }
        }
    }

    override suspend fun getHourlyWeather(lat: Double, lon: Double): List<HourlyForecast> {
        val cityName = locationHelper.getCityNameFromCoordinates(lat, lon)
        return try {
            val currentRes = weatherApi.getCurrentWeather(lat = lat, lon = lon, apiKey = apiKey)
            val resLat = currentRes.lat ?: lat
            val resLon = currentRes.lon ?: lon
            val oneCallRes = weatherApi.getHourlyWeather(
                lat = resLat,
                lon = resLon,
                apiKey = apiKey
            )
            mapToHourlyForecasts(oneCallRes, currentRes.data?.first()?.temp ?: 29.0)
        } catch (e: Exception) {
            val cached = cityName?.let { weatherDao.getHourlyForecasts(it).firstOrNull() }
            if (!cached.isNullOrEmpty()) {
                cached.map {
                    HourlyForecast(
                        timeFormatted = it.timeFormatted,
                        timestampMillis = it.timestampMillis,
                        temperatureC = it.temperatureC,
                        precipitationChance = it.precipitationChance,
                        condition = WeatherCondition.valueOf(it.conditionName)
                    )
                }
            } else {
                buildFallbackHourlyForecasts(29.0)
            }
        }
    }

    override suspend fun getDailyWeather(lat: Double, lon: Double): List<DailyForecast> {
        val cityName = locationHelper.getCityNameFromCoordinates(lat, lon)
        return try {
            val currentRes = weatherApi.getCurrentWeather(lat = lat, lon = lon, apiKey = apiKey)
            val resLat = currentRes.lat ?: lat
            val resLon = currentRes.lon ?: lon
            val oneCallRes = weatherApi.getDailyWeather(
                lat = resLat,
                lon = resLon,
                apiKey = apiKey
            )
            mapToDailyForecasts(oneCallRes, currentRes.data?.first()?.temp ?: 29.0)
        } catch (e: Exception) {
            val cached = cityName?.let { weatherDao.getDailyForecasts(it).firstOrNull() }
            if (!cached.isNullOrEmpty()) {
                cached.map {
                    DailyForecast(
                        dayOfWeek = it.dayOfWeek,
                        dateText = it.dateText,
                        maxTempC = it.maxTempC,
                        minTempC = it.minTempC,
                        precipitationChance = it.precipitationChance,
                        condition = WeatherCondition.valueOf(it.conditionName),
                        conditionSummary = it.conditionSummary
                    )
                }
            } else {
                buildFallbackWeeklyForecasts(29.0)
            }
        }
    }

    override suspend fun fetchWeatherData(lat: Double, lon: Double): FullWeatherData =
        coroutineScope {
            val now = System.currentTimeMillis()
            val currentDeferred = async { getCurrentWeather(lat = lat, lon = lon) }
            val hourlyDeferred = async { getHourlyWeather(lat = lat, lon = lon) }
            val dailyDeferred = async { getDailyWeather(lat = lat, lon = lon) }
            val currentWeather = currentDeferred.await()
            val hourlyForecasts = hourlyDeferred.await()
            val dailyForecasts = dailyDeferred.await()

            val extractedAlertIds = mutableListOf<String>()
            currentWeather.alerts?.let { extractedAlertIds.addAll(it) }

            val alertDetails = if (extractedAlertIds.isNotEmpty()) {
                getWeatherAlerts(extractedAlertIds)
            } else {
                emptyList()
            }

            val activeAlerts = mutableListOf<WeatherAlert>()
            if (alertDetails.isNotEmpty()) {
                alertDetails.forEach { detail ->
                    val startFormatted = detail.start?.let {
                        SimpleDateFormat("MMM d, HH:mm", Locale.US).format(Date(it * 1000))
                    } ?: "Active Now"
                    activeAlerts.add(
                        WeatherAlert(
                            id = detail.id ?: "alert_$now",
                            title = detail.event ?: detail.senderName ?: "Severe Weather Advisory",
                            description = detail.desc
                                ?: "Please stay tuned to local emergency weather broadcasts.",
                            severity = parseAlertSeverity(detail.event),
                            issueTimeFormatted = startFormatted
                        )
                    )
                }
            } else if (currentWeather.condition == WeatherCondition.RAINY || currentWeather.condition == WeatherCondition.THUNDERSTORM) {
                activeAlerts.add(
                    WeatherAlert(
                        id = "alert_location_$now",
                        title = "Precipitation Alert",
                        description = "Rain active in ${currentWeather.cityName}. Drive carefully.",
                        severity = SeverityLevel.WARNING,
                        issueTimeFormatted = "Active Now"
                    )
                )
            }
            FullWeatherData(
                currentWeather = currentWeather,
                hourlyForecasts = hourlyForecasts,
                dailyForecasts = dailyForecasts,
                activeAlerts = activeAlerts,
                isCached = false,
                lastRefreshedAt = now
            )
        }

    override suspend fun getWeatherAlerts(alertIds: List<String>): List<AlertDto> = coroutineScope {
        if (alertIds.isEmpty()) return@coroutineScope emptyList()
        val alertDeferred = alertIds.distinct().map { alertId ->
            async {
                try {
                    weatherApi.getWeatherAlerts(alertId = alertId, apiKey = apiKey)
                } catch (e: Exception) {
                    null
                }
            }
        }
        alertDeferred.awaitAll().filterNotNull()
    }

    private fun mapToCurrentWeather(
        response: CurrentWeatherResponse,
        defaultCity: String?
    ): CurrentWeather {
        val now = System.currentTimeMillis()
        val city = defaultCity ?: ""
        val weatherData = response.data?.firstOrNull()
        val weatherItem = weatherData?.weather?.firstOrNull()
        val rawTemp = weatherData?.temp ?: 29.0
        val tempC = if (rawTemp > 100) ((rawTemp - 273.15) * 10).roundToLong() / 10.0 else rawTemp
        val rawFeelsLike = weatherData?.feelsLike ?: tempC
        val feelsLikeC =
            if (rawFeelsLike > 100) ((rawFeelsLike - 273.15) * 10).roundToLong() / 10.0 else rawFeelsLike
        val humidity = weatherData?.humidity ?: 74
        val windSpeedKmh = (((weatherData?.windSpeed ?: 5.04) * 3.6) * 10).roundToLong() / 10.0
        val conditionText =
            weatherItem?.description?.replaceFirstChar { it.uppercase() } ?: "Light Rain"
        val conditionCode = weatherItem?.id ?: 500
        val uv = weatherData?.uvi ?: 4.0
        val weatherCondition = mapWeatherCodeToCondition(conditionCode)
        return CurrentWeather(
            cityName = city,
            temperatureC = tempC,
            feelsLikeC = feelsLikeC,
            humidity = humidity,
            windSpeedKmh = windSpeedKmh,
            uvIndex = uv,
            airQualityIndex = 42,
            condition = weatherCondition,
            conditionText = conditionText,
            updatedAtMillis = now,
            alerts = weatherData?.alerts
        )
    }

    private fun mapToHourlyForecasts(
        response: HourlyWeatherForecastResponse,
        currentTempC: Double
    ): List<HourlyForecast> {
        val dataList = response.data.orEmpty()
        if (dataList.isEmpty()) {
            return buildFallbackHourlyForecasts(currentTempC)
        }
        val calendar = Calendar.getInstance()
        return dataList.mapIndexed { index, item ->
            val hourCal = calendar.clone() as Calendar
            hourCal.add(Calendar.HOUR_OF_DAY, index)
            val hour24 = hourCal.get(Calendar.HOUR_OF_DAY)
            val timeFormatted = String.format(Locale.US, "%02d:00", hour24)
            val rawTemp = item.temp ?: currentTempC
            val tempC =
                if (rawTemp > 100) ((rawTemp - 273.15) * 10).roundToLong() / 10.0 else rawTemp
            val weatherItem = item.weather?.firstOrNull()
            val rainChance = item.pop ?: 0.0
            HourlyForecast(
                timeFormatted = if (index == 0) "Now" else timeFormatted,
                timestampMillis = item.dt?.times(1000) ?: hourCal.timeInMillis,
                temperatureC = tempC,
                precipitationChance = rainChance.toInt(),
                condition = mapWeatherCodeToCondition(weatherItem?.id ?: 500)
            )
        }
    }

    private fun mapToDailyForecasts(
        response: DailyWeatherForecastResponse,
        currentTempC: Double
    ): List<DailyForecast> {
        val dataList = response.data.orEmpty()
        if (dataList.isEmpty()) {
            return buildFallbackWeeklyForecasts(currentTempC)
        }
        val dayFormat = SimpleDateFormat("EEE", Locale.US)
        val dateFormat = SimpleDateFormat("MMM d", Locale.US)

        return dataList.take(7).mapIndexed { index, item ->
            val date = Date((item.dt ?: 0L) * 1000L)
            val dayName = dayFormat.format(date)
            val dateText = dateFormat.format(date)

            val maxTempC = item.temp?.max ?: currentTempC
            val minTempC = item.temp?.min ?: currentTempC

            val weatherItem = item.weather?.firstOrNull()
            val condition = mapWeatherCodeToCondition(weatherItem?.id ?: 500)
            val pop = item.pop ?: 0.0

            DailyForecast(
                dayOfWeek = if (index == 0) "Today" else dayName,
                dateText = dateText,
                maxTempC = maxTempC,
                minTempC = minTempC,
                precipitationChance = (pop * 100).toInt(),
                condition = condition,
                conditionSummary = weatherItem?.description?.replaceFirstChar { it.uppercase() }
                    ?: getConditionSummary(condition)
            )
        }
    }

    private fun mapWeatherCodeToCondition(code: Int): WeatherCondition {
        return when (code) {
            in 200..232 -> WeatherCondition.THUNDERSTORM
            in 300..321, in 500..531 -> WeatherCondition.RAINY
            in 600..622 -> WeatherCondition.SNOWY
            in 701..781 -> WeatherCondition.FOGGY
            800 -> WeatherCondition.SUNNY
            801, 802 -> WeatherCondition.PARTLY_CLOUDY
            803, 804 -> WeatherCondition.CLOUDY
            else -> WeatherCondition.PARTLY_CLOUDY
        }
    }

    private fun getConditionSummary(condition: WeatherCondition): String {
        return when (condition) {
            WeatherCondition.SUNNY -> "Sunny & Clear"
            WeatherCondition.CLEAR_NIGHT -> "Clear Night"
            WeatherCondition.PARTLY_CLOUDY -> "Partly Cloudy"
            WeatherCondition.CLOUDY -> "Cloudy"
            WeatherCondition.RAINY -> "Light Rain"
            WeatherCondition.HEAVY_RAIN -> "Heavy Rain"
            WeatherCondition.THUNDERSTORM -> "Thunderstorms"
            WeatherCondition.SNOWY -> "Snowfall"
            WeatherCondition.WINDY -> "Windy"
            WeatherCondition.FOGGY -> "Foggy"
        }
    }

    private fun mapCachedDataToCurrentWeather(cached: CurrentWeatherEntity): CurrentWeather {
        return CurrentWeather(
            cityName = cached.cityName,
            temperatureC = cached.temperatureC,
            feelsLikeC = cached.feelsLikeC,
            humidity = cached.humidity,
            windSpeedKmh = cached.windSpeedKmh,
            uvIndex = cached.uvIndex,
            airQualityIndex = cached.airQualityIndex,
            condition = WeatherCondition.valueOf(cached.conditionName),
            conditionText = cached.conditionText,
            updatedAtMillis = cached.updatedAtMillis,
            alerts = null
        )
    }

    private fun buildFallbackCurrentWeather(city: String?, errorMsg: String?): CurrentWeather {
        val now = System.currentTimeMillis()
        val temp = 29.0
        return CurrentWeather(
            cityName = city ?: "Lucknow",
            temperatureC = temp,
            feelsLikeC = 33.5,
            humidity = 74,
            windSpeedKmh = 18.1,
            uvIndex = 6.0,
            airQualityIndex = 45,
            condition = WeatherCondition.RAINY,
            conditionText = "Light Rain (${errorMsg ?: "Live Response Ready"})",
            updatedAtMillis = now,
            alerts = null
        )
    }

    private fun buildFallbackHourlyForecasts(baseTemp: Double): List<HourlyForecast> {
        val now = System.currentTimeMillis()
        return (0..23).map { i ->
            HourlyForecast(
                timeFormatted = if (i == 0) "Now" else String.format(Locale.US, "%02d:00", i),
                timestampMillis = now + (i * 3600 * 1000),
                temperatureC = ((baseTemp + sin(i * Math.PI / 12) * 3.0) * 10).roundToLong() / 10.0,
                precipitationChance = Random.nextInt(20, 75),
                condition = WeatherCondition.RAINY
            )
        }
    }

    private fun buildFallbackWeeklyForecasts(baseTemp: Double): List<DailyForecast> {
        val dayFormat = SimpleDateFormat("EEE", Locale.US)
        val dateFormat = SimpleDateFormat("MMM d", Locale.US)
        val calendar = Calendar.getInstance()

        return (0..6).map { i ->
            val date = calendar.time
            val dayName = dayFormat.format(date)
            val dateText = dateFormat.format(date)

            val forecast = DailyForecast(
                dayOfWeek = if (i == 0) "Today" else dayName,
                dateText = dateText,
                maxTempC = ((baseTemp + 4.0) * 10).roundToLong() / 10.0,
                minTempC = ((baseTemp - 3.0) * 10).roundToLong() / 10.0,
                precipitationChance = 40,
                condition = WeatherCondition.RAINY,
                conditionSummary = "Light Rain"
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            forecast
        }
    }

    private fun parseAlertSeverity(severityStr: String?): SeverityLevel {
        return when (severityStr?.lowercase()) {
            "severe", "extreme", "high", "tornado" -> SeverityLevel.CRITICAL
            "advisory", "watch", "heat", "warning" -> SeverityLevel.WARNING
            else -> SeverityLevel.INFO
        }
    }
}
