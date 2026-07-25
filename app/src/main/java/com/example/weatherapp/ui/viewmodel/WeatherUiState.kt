package com.example.weatherapp.ui.viewmodel

import com.example.weatherapp.domain.model.FullWeatherData

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(
        val weatherData: FullWeatherData,
        val isOffline: Boolean,
        val selectedUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
        val isRefreshing: Boolean = false
    ) : WeatherUiState
    data class Error(
        val message: String,
        val cachedData: FullWeatherData? = null
    ) : WeatherUiState
}
