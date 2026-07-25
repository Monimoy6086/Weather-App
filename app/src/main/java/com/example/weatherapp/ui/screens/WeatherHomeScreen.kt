package com.example.weatherapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.screens.components.CurrentWeatherCard
import com.example.weatherapp.ui.screens.components.HourlyForecastRow
import com.example.weatherapp.ui.screens.components.OfflineStatusBadge
import com.example.weatherapp.ui.screens.components.SearchBarComponent
import com.example.weatherapp.ui.screens.components.SevereAlertBanner
import com.example.weatherapp.ui.screens.components.WeeklyForecastList
import com.example.weatherapp.ui.theme.DeepNavyDark
import com.example.weatherapp.ui.theme.SoftCyanAccent
import com.example.weatherapp.ui.theme.TextSubtle
import com.example.weatherapp.ui.theme.TextWhite
import com.example.weatherapp.ui.viewmodel.TemperatureUnit
import com.example.weatherapp.ui.viewmodel.WeatherUiState
import com.example.weatherapp.ui.viewmodel.WeatherViewModel
import com.example.weatherapp.utils.LocationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomeScreen(
    viewModel: WeatherViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.fetchLocationAndLoadWeather()
        }
    }

    LaunchedEffect(Unit) {
        if (!LocationHelper(context).hasLocationPermission()) {
            permissionLauncher.launch(LocationHelper.REQUIRED_PERMISSIONS)
        }
        viewModel.errorEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Weather Forecast",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleTemperatureUnit() }) {
                        val currentUnitText = if (uiState is WeatherUiState.Success && (uiState as WeatherUiState.Success).selectedUnit == TemperatureUnit.FAHRENHEIT) "°F" else "°C"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftCyanAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = currentUnitText,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = SoftCyanAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.onRefresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = SoftCyanAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepNavyDark
                )
            )
        },
        containerColor = DeepNavyDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SearchBarComponent(
                onSearch = { query -> viewModel.searchCity(query) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = SoftCyanAccent)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Fetching weather forecast...", color = TextSubtle)
                        }
                    }
                }

                is WeatherUiState.Success -> {
                    if (state.isOffline) {
                        OfflineStatusBadge(
                            lastRefreshedAt = state.weatherData.lastRefreshedAt
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Current Weather Hero Card
                    CurrentWeatherCard(
                        weather = state.weatherData.currentWeather,
                        unit = state.selectedUnit
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Severe Alerts Banner
                    SevereAlertBanner(alerts = state.weatherData.activeAlerts)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hourly Forecast Carousel
                    HourlyForecastRow(
                        hourlyForecasts = state.weatherData.hourlyForecasts,
                        unit = state.selectedUnit
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 7-Day Forecast List
                    WeeklyForecastList(
                        dailyForecasts = state.weatherData.dailyForecasts,
                        unit = state.selectedUnit
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                is WeatherUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Error: ${state.message}",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Red)
                            )
                        }
                    }
                }
            }
        }
    }
}
