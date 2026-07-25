package com.example.weatherapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey val cityName: String,
    val temperatureC: Double,
    val feelsLikeC: Double,
    val humidity: Int,
    val windSpeedKmh: Double,
    val uvIndex: Double,
    val airQualityIndex: Int,
    val conditionName: String,
    val conditionText: String,
    val updatedAtMillis: Long
)

@Entity(tableName = "hourly_forecast")
data class HourlyForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityName: String,
    val timeFormatted: String,
    val timestampMillis: Long,
    val temperatureC: Double,
    val precipitationChance: Int,
    val conditionName: String
)

@Entity(tableName = "daily_forecast")
data class DailyForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityName: String,
    val dayOfWeek: String,
    val dateText: String,
    val maxTempC: Double,
    val minTempC: Double,
    val precipitationChance: Int,
    val conditionName: String,
    val conditionSummary: String
)

@Entity(tableName = "weather_alerts")
data class WeatherAlertEntity(
    @PrimaryKey val alertId: String,
    val cityName: String,
    val title: String,
    val description: String,
    val severity: String,
    val issueTimeFormatted: String
)
