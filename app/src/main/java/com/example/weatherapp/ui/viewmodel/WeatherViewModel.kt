package com.example.weatherapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.model.Resource
import com.example.weatherapp.utils.LocationCoordinates
import com.example.weatherapp.utils.LocationHelper
import com.example.weatherapp.worker.WeatherSyncWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val locationHelper: LocationHelper,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _currentLocation = MutableStateFlow<LocationCoordinates?>(null)
    val currentLocation: StateFlow<LocationCoordinates?> = _currentLocation.asStateFlow()

    private val _currentCity = MutableStateFlow("Lucknow")
    val currentCity: StateFlow<String> = _currentCity.asStateFlow()

    private val _temperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)

    private var weatherJob: Job? = null

    private val _errorEvents = Channel<String>()
    val errorEvents = _errorEvents.receiveAsFlow()

    init {
        fetchLocationAndLoadWeather()
//        loadWeatherForecast(_currentCity.value)
//        scheduleBackgroundSync(_currentCity.value)
    }

    fun fetchLocationAndLoadWeather(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!locationHelper.hasLocationPermission()) {
                _uiState.value = WeatherUiState.Error(
                    message = "Location permission not granted.",
                    cachedData = null
                )
                return@launch
            }
            _uiState.value = WeatherUiState.Loading
            val coordinates = locationHelper.getCurrentLocation()
            _currentLocation.value = coordinates
            loadWeatherForecast(
                lat = coordinates.latitude,
                lon = coordinates.longitude,
                city = coordinates.cityName,
                forceRefresh = forceRefresh
            )
            scheduleBackgroundSync(coordinates.latitude, coordinates.longitude)
        }
    }

    fun loadWeatherForecast(lat: Double, lon: Double, city: String, forceRefresh: Boolean = false) {
        _currentCity.value = city
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            repository.getWeatherForecastStream(lat = lat, lon = lon, forceRefresh = forceRefresh)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            if (_uiState.value !is WeatherUiState.Success) {
                                _uiState.value = WeatherUiState.Loading
                            }
                        }

                        is Resource.Success -> {
                            _uiState.value = WeatherUiState.Success(
                                weatherData = resource.data,
                                isOffline = resource.isOffline,
                                selectedUnit = _temperatureUnit.value,
                                isRefreshing = false
                            )
                        }

                        is Resource.Error -> {
                            _uiState.value = WeatherUiState.Error(
                                message = resource.message,
                                cachedData = resource.cachedData
                            )
                        }
                    }
                }
        }
    }

    fun onRefresh() {
        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            _uiState.value = currentState.copy(isRefreshing = true)
        }
        val loc = _currentLocation.value
        if (loc != null) {
            loadWeatherForecast(
                lat = loc.latitude,
                lon = loc.longitude,
                city = loc.cityName.ifBlank { _currentCity.value },
                forceRefresh = true
            )
        } else {
            fetchLocationAndLoadWeather(forceRefresh = true)
        }
//        loadWeatherForecast(_currentCity.value, forceRefresh = true)
    }

    fun toggleTemperatureUnit() {
        val newUnit = if (_temperatureUnit.value == TemperatureUnit.CELSIUS) {
            TemperatureUnit.FAHRENHEIT
        } else {
            TemperatureUnit.CELSIUS
        }
        _temperatureUnit.value = newUnit

        val currentState = _uiState.value
        if (currentState is WeatherUiState.Success) {
            _uiState.value = currentState.copy(selectedUnit = newUnit)
        }
    }

    fun searchCity(cityName: String) {
        if (cityName.isNotBlank()) {
            _currentCity.value = cityName
            weatherJob?.cancel()
            weatherJob = viewModelScope.launch {
                val previousState = _uiState.value
                _uiState.value = WeatherUiState.Loading
                // Use Geocoder to resolve latitude and longitude from city name
                val geocodedCoords = locationHelper.getCoordinatesFromCityName(cityName)
                if (geocodedCoords != null) {
                    _currentLocation.value = geocodedCoords
                    repository.getWeatherForecastStream(
                        lat = geocodedCoords.latitude,
                        lon = geocodedCoords.longitude,
                        forceRefresh = true
                    ).collect { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                _uiState.value = WeatherUiState.Loading
                            }

                            is Resource.Success -> {
                                _uiState.value = WeatherUiState.Success(
                                    weatherData = resource.data,
                                    isOffline = resource.isOffline,
                                    selectedUnit = _temperatureUnit.value,
                                    isRefreshing = false
                                )
                                scheduleBackgroundSync(geocodedCoords.latitude, geocodedCoords.longitude)
                            }

                            is Resource.Error -> {
                                _errorEvents.send("Weather Data Not Available")
                                _uiState.value = WeatherUiState.Error(
                                    message = resource.message,
                                    cachedData = resource.cachedData
                                )
                            }
                        }
                    }
                } else {
                    _errorEvents.send("Weather Data Not Available")
                    _uiState.value = previousState
                }
            }
        }
    }

    private fun scheduleBackgroundSync(lat: Double, lon: Double) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncData = Data.Builder()
            .putDouble(WeatherSyncWorker.KEY_LAT, lat)
            .putDouble(WeatherSyncWorker.KEY_LON, lon)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<WeatherSyncWorker>(
            repeatInterval = 2,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInputData(syncData)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WeatherSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            syncRequest
        )
    }
}

