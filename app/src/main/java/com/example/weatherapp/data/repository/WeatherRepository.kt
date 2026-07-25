package com.example.weatherapp.data.repository

import com.example.weatherapp.domain.model.FullWeatherData
import com.example.weatherapp.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherForecastStream(
        lat: Double,
        lon: Double,
        forceRefresh: Boolean = false
    ): Flow<Resource<FullWeatherData>>
    suspend fun refreshWeatherCache(lat: Double, lon: Double): Result<Unit>
}
