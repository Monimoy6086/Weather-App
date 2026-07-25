package com.example.weatherapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {

    @GET("current")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ) : CurrentWeatherResponse

    @GET("timeline/1h")
    suspend fun getHourlyWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ) : HourlyWeatherForecastResponse

    @GET("timeline/1day")
    suspend fun getDailyWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ) : DailyWeatherForecastResponse

    @GET("alert/{alert_id}")
    suspend fun getWeatherAlerts(
        @Path("alert_id") alertId: String,
        @Query("appid") apiKey: String
    ) : AlertDto

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/4.0/onecall/"
    }
}