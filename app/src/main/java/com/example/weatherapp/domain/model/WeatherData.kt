package com.example.weatherapp.domain.model

enum class WeatherCondition {
    SUNNY,
    CLEAR_NIGHT,
    PARTLY_CLOUDY,
    CLOUDY,
    RAINY,
    HEAVY_RAIN,
    THUNDERSTORM,
    SNOWY,
    WINDY,
    FOGGY
}

enum class SeverityLevel {
    INFO,
    WARNING,
    CRITICAL
}

data class CurrentWeather(
    val cityName: String,
    val temperatureC: Double,
    val feelsLikeC: Double,
    val humidity: Int,
    val windSpeedKmh: Double,
    val uvIndex: Double,
    val airQualityIndex: Int,
    val condition: WeatherCondition,
    val conditionText: String,
    val updatedAtMillis: Long,
    val alerts: List<String>?
)

data class HourlyForecast(
    val timeFormatted: String,
    val timestampMillis: Long,
    val temperatureC: Double,
    val precipitationChance: Int,
    val condition: WeatherCondition
)

data class DailyForecast(
    val dayOfWeek: String,
    val dateText: String,
    val maxTempC: Double,
    val minTempC: Double,
    val precipitationChance: Int,
    val condition: WeatherCondition,
    val conditionSummary: String
)

data class WeatherAlert(
    val id: String,
    val title: String,
    val description: String,
    val severity: SeverityLevel,
    val issueTimeFormatted: String
)

data class FullWeatherData(
    val currentWeather: CurrentWeather,
    val hourlyForecasts: List<HourlyForecast>,
    val dailyForecasts: List<DailyForecast>,
    val activeAlerts: List<WeatherAlert>,
    val isCached: Boolean,
    val lastRefreshedAt: Long
)

sealed interface Resource<out T> {
    data class Success<T>(val data: T, val isOffline: Boolean = false) : Resource<T>
    data class Error<T>(val message: String, val cachedData: T? = null) : Resource<T>
    data object Loading : Resource<Nothing>
}
