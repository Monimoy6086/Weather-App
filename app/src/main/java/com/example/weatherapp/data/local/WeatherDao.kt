package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.DailyForecastEntity
import com.example.weatherapp.data.local.entities.HourlyForecastEntity
import com.example.weatherapp.data.local.entities.WeatherAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM current_weather WHERE cityName = :cityName LIMIT 1")
    fun getCurrentWeather(cityName: String): Flow<CurrentWeatherEntity?>

    @Query("SELECT * FROM hourly_forecast WHERE cityName = :cityName ORDER BY timestampMillis ASC")
    fun getHourlyForecasts(cityName: String): Flow<List<HourlyForecastEntity>>

    @Query("SELECT * FROM daily_forecast WHERE cityName = :cityName ORDER BY id ASC")
    fun getDailyForecasts(cityName: String): Flow<List<DailyForecastEntity>>

    @Query("SELECT * FROM weather_alerts WHERE cityName = :cityName")
    fun getWeatherAlerts(cityName: String): Flow<List<WeatherAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(weather: CurrentWeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyForecasts(hourly: List<HourlyForecastEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyForecasts(daily: List<DailyForecastEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherAlerts(alerts: List<WeatherAlertEntity>)

    @Query("DELETE FROM hourly_forecast WHERE cityName = :cityName")
    suspend fun clearHourlyForCity(cityName: String)

    @Query("DELETE FROM daily_forecast WHERE cityName = :cityName")
    suspend fun clearDailyForCity(cityName: String)

    @Query("DELETE FROM weather_alerts WHERE cityName = :cityName")
    suspend fun clearAlertsForCity(cityName: String)

    @Transaction
    suspend fun updateFullWeatherCache(
        current: CurrentWeatherEntity,
        hourly: List<HourlyForecastEntity>,
        daily: List<DailyForecastEntity>,
        alerts: List<WeatherAlertEntity>
    ) {
        insertCurrentWeather(current)
        clearHourlyForCity(current.cityName)
        insertHourlyForecasts(hourly)
        clearDailyForCity(current.cityName)
        insertDailyForecasts(daily)
        clearAlertsForCity(current.cityName)
        insertWeatherAlerts(alerts)
    }
}
